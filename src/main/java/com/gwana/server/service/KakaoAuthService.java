package com.gwana.server.service;

import com.gwana.server.client.KakaoApiClient;
import com.gwana.server.client.KakaoAuthClient;
import com.gwana.server.common.config.KakaoProperties;
import com.gwana.server.common.enums.Role;
import com.gwana.server.common.exception.UserException;
import com.gwana.server.dto.auth.LoginResult;
import com.gwana.server.dto.kakao.KakaoTokenResponse;
import com.gwana.server.dto.kakao.KakaoUserResponse;
import com.gwana.server.dto.socialAccount.SocialAccountRequest;
import com.gwana.server.dto.token.TokenResponse;
import com.gwana.server.dto.user.SocialUser;
import com.gwana.server.dto.user.UserDto;
import com.gwana.server.mapper.UserMapper;
import io.hypersistence.tsid.TSID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * 카카오 로그인 오케스트레이션.
 * <p>
 * 정책:
 * <ul>
 *   <li>카카오 <b>이메일 제공은 필수</b>. 미동의(이메일 null)면 재동의 안내 예외.</li>
 *   <li><b>이메일은 전역 유일 키</b>(users.email UNIQUE).</li>
 *   <li>재방문 카카오 사용자는 <b>(provider, providerId)</b> 로 식별해 로그인.</li>
 *   <li>최초 카카오 로그인인데 <b>동일 이메일 계정이 이미 있으면</b> 자동 생성/연동하지 않고
 *       "기존 계정으로 로그인" 을 안내하는 예외를 던진다(계정 탈취/중복가입 방지).</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoAuthService {
    private static final String PROVIDER = "kakao";

    private final KakaoAuthClient kakaoAuthClient;
    private final KakaoApiClient kakaoApiClient;
    private final KakaoProperties kakaoProperties;
    private final UserMapper userMapper;
    private final TokenService tokenService;

    @Transactional
    public LoginResult login(String code) {
        KakaoTokenResponse kakaoToken = kakaoAuthClient.issueToken(
                "authorization_code",
                kakaoProperties.clientId(),
                kakaoProperties.clientSecret(),
                kakaoProperties.redirectUri(),
                code
        );

        KakaoUserResponse kakaoUser = kakaoApiClient.getUser("Bearer " + kakaoToken.accessToken());
        Long providerId = kakaoUser.id();
        String email = kakaoUser.kakaoAccount() != null ? kakaoUser.kakaoAccount().email() : null;

        // 이메일 필수 — 미동의 시 재동의 안내
        if (email == null || email.isBlank()) {
            throw new UserException.KakaoEmailRequiredException();
        }

        // 1) 이미 카카오로 가입한 사용자 → 로그인 (토큰 최신화)
        Optional<SocialUser> bySocial = userMapper.findUserByProvider(PROVIDER, providerId);
        if (bySocial.isPresent()) {
            SocialUser user = bySocial.get();
            userMapper.updateSocialAccessToken(user.getSocialAccountId(), kakaoToken.accessToken());
            return new LoginResult(user, tokenService.issueTokens(user.getUserId()));
        }

        // 2) 최초 카카오 로그인: 동일 이메일 계정이 이미 있으면 → 기존 계정 로그인 유도
        if (userMapper.findUserByEmail(email).isPresent()) {
            throw new UserException.EmailAlreadyRegisteredException();
        }

        // 3) 신규 가입
        SocialUser user = register(kakaoUser, email, kakaoToken.accessToken());
        return new LoginResult(user, tokenService.issueTokens(user.getUserId()));
    }

    /**
     * 신규 카카오 사용자 등록: users + social_account 를 함께 생성한다.
     * 동시 최초 로그인으로 인한 유니크 충돌은 provider/email 을 재확인해 안전하게 처리한다.
     */
    private SocialUser register(KakaoUserResponse kakaoUser, String email, String kakaoAccessToken) {
        Long providerId = kakaoUser.id();
        KakaoUserResponse.KakaoAccount account = kakaoUser.kakaoAccount();

        UserDto user = UserDto.builder()
                .userId(TSID.Factory.getTsid().toString())
                .customerKey(UUID.randomUUID().toString())
                .username(resolveUsername(account))
                .email(email)
                .phone(normalizePhone(account != null ? account.phoneNumber() : null))
                .role(Role.GENERAL)
                .build();

        SocialAccountRequest socialAccount = SocialAccountRequest.builder()
                .socialAccountId(TSID.Factory.getTsid().toString())
                .userId(user.getUserId())
                .provider(PROVIDER)
                .providerId(providerId)
                .accessToken(kakaoAccessToken)
                .build();

        try {
            userMapper.createUser(user);
            userMapper.createSocialAccount(socialAccount);
        } catch (DuplicateKeyException duplicate) {
            // 동시성으로 인한 충돌: 카카오로 이미 만들어졌으면 그 계정으로 로그인, 아니면 이메일 중복 안내
            log.warn("카카오 계정 동시 생성 충돌. providerId={}", providerId);
            return userMapper.findUserByProvider(PROVIDER, providerId)
                    .orElseThrow(UserException.EmailAlreadyRegisteredException::new);
        }

        return userMapper.findUserByUserId(user.getUserId())
                .orElseThrow(UserException.UserNotExistException::new);
    }

    private String resolveUsername(KakaoUserResponse.KakaoAccount account) {
        if (account != null && account.name() != null && !account.name().isBlank()) {
            return account.name();
        }
        return "카카오회원";
    }

    /**
     * 카카오 phone_number("+82 10-1234-5678") → "01012345678" 형태로 정규화.
     */
    private String normalizePhone(String phone) {
        if (phone == null) {
            return null;
        }
        String digits = phone.replaceAll("[^0-9]", "");
        if (digits.startsWith("82")) {
            digits = "0" + digits.substring(2);
        }
        return digits.isEmpty() ? null : digits;
    }
}
