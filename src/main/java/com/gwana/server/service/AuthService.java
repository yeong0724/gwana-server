package com.gwana.server.service;

import com.gwana.server.client.KakaoApiClient;
import com.gwana.server.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private static final String PROVIDER_KAKAO = "kakao";

    private final UserMapper userMapper;
    private final KakaoApiClient kakaoApiClient;
    private final TokenService tokenService;

    /**
     * 로그아웃: 서버측 세션(Refresh Token)을 폐기하고, 저장된 카카오 토큰이 있으면 카카오 로그아웃을 시도한다.
     * 카카오 로그아웃 실패는 우리 서비스 세션 폐기를 막지 않는다(best-effort).
     */
    @Transactional
    public void logout(String userId) {
        String kakaoAccessToken = userMapper.findSocialAccessToken(userId, PROVIDER_KAKAO);

        if (kakaoAccessToken != null && !kakaoAccessToken.isBlank()) {
            try {
                kakaoApiClient.logout("Bearer " + kakaoAccessToken);
            } catch (Exception e) {
                log.warn("카카오 로그아웃 실패(무시하고 세션 폐기 진행). userId={}, cause={}", userId, e.getMessage());
            }
        }

        tokenService.deleteRefreshToken(userId);
    }
}
