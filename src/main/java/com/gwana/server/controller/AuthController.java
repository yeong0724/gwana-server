package com.gwana.server.controller;

import com.gwana.server.common.security.JwtTokenProvider;
import com.gwana.server.common.utils.ApiResponse;
import com.gwana.server.common.utils.TokenCookieManager;
import com.gwana.server.dto.auth.KakaoLoginRequest;
import com.gwana.server.dto.auth.LoginResult;
import com.gwana.server.dto.token.TokenResponse;
import com.gwana.server.dto.user.AuthUser;
import com.gwana.server.dto.user.LoginResponse;
import com.gwana.server.dto.user.SocialUser;
import com.gwana.server.service.AuthService;
import com.gwana.server.service.KakaoAuthService;
import com.gwana.server.service.TokenService;
import com.gwana.server.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

/**
 * 인증(로그인/토큰 재발급/로그아웃) 엔드포인트.
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "인증", description = "로그인 / 토큰 재발급 / 로그아웃")
public class AuthController {
    private final KakaoAuthService kakaoAuthService;
    private final AuthService authService;
    private final TokenService tokenService;
    private final UserService userService;
    private final TokenCookieManager tokenCookieManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${app.kakao.logout-uri}")
    private String kakaoLogoutUri;

    @Value("${app.kakao.logout-redirect-uri}")
    private String kakaoLogoutRedirectUri;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    /**
     * 카카오 로그인: 인가 코드로 로그인/회원가입을 처리하고 Access Token 반환 + Refresh Token 쿠키 설정.
     */
    @Operation(summary = "카카오 로그인", description = "인가 코드로 로그인/회원가입 처리 후 Access Token 반환 + Refresh Token 쿠키 설정")
    @PostMapping("/kakao/login")
    public ApiResponse<LoginResponse> kakaoLogin(
            @RequestBody KakaoLoginRequest request,
            HttpServletResponse response
    ) {
        LoginResult result = kakaoAuthService.login(request.code());
        tokenCookieManager.setRefreshTokenCookie(response, result.tokens().getRefreshToken());
        return ApiResponse.ok(toLoginResponse(result.user(), result.tokens().getAccessToken()));
    }

    /**
     * Access Token 재발급: Refresh Token 쿠키만으로 검증·회전한다.
     */
    @Operation(summary = "Access Token 재발급", description = "Refresh Token 쿠키만으로 검증·회전하여 새 Access Token 발급")
    @PostMapping("/token/refresh")
    public ApiResponse<LoginResponse> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        TokenResponse tokens = tokenService.reissue(refreshToken);
        tokenCookieManager.setRefreshTokenCookie(response, tokens.getRefreshToken());

        SocialUser user = userService.findUserByUserId(tokens.getUserId());
        return ApiResponse.ok(toLoginResponse(user, tokens.getAccessToken()));
    }

    /**
     * 로그아웃: 서버 Refresh Token 폐기 + 카카오 로그아웃(best-effort) + 쿠키 제거.
     * 인증된 사용자만 호출 가능(Bearer Access Token 필요).
     */
    @Operation(summary = "로그아웃", description = "Refresh Token 폐기 + 카카오 로그아웃(best-effort) + 쿠키 제거 (Bearer 필요)")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletResponse response) {
        AuthUser authUser = jwtTokenProvider.getUserInfo();
        if (authUser != null) {
            authService.logout(authUser.getUserId());
        }
        tokenCookieManager.clearRefreshTokenCookie(response);
        return ApiResponse.ok(null);
    }

    /**
     * 카카오 SSO 로그아웃 페이지로 리다이렉트(브라우저 레벨 로그아웃). 완료 후 프론트 로그아웃 페이지로 복귀한다.
     */
    @Operation(summary = "카카오 SSO 로그아웃 리다이렉트", description = "카카오 로그아웃 페이지로 302 리다이렉트")
    @GetMapping("/oauth2/logout/kakao")
    public void kakaoLogoutRedirect(HttpServletResponse response) {
        String kakaoLogoutUrl = UriComponentsBuilder.fromUriString(kakaoLogoutUri)
                .queryParam("client_id", kakaoClientId)
                .queryParam("logout_redirect_uri", kakaoLogoutRedirectUri)
                .toUriString();

        try {
            response.sendRedirect(kakaoLogoutUrl);
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private LoginResponse toLoginResponse(SocialUser user, String accessToken) {
        return LoginResponse.builder()
                .accessToken(accessToken)
                .provider(user.getProvider())
                .customerKey(user.getCustomerKey())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .profileImage(user.getProfileImage())
                .zonecode(user.getZonecode())
                .roadAddress(user.getRoadAddress())
                .detailAddress(user.getDetailAddress())
                .role(user.getRole())
                .build();
    }
}
