package com.gwana.server.controller;

import com.gwana.server.common.security.JwtTokenProvider;
import com.gwana.server.common.utils.ApiResponse;
import com.gwana.server.common.utils.TokenCookieManager;
import com.gwana.server.dto.socialAccount.SocialAccountRequest;
import com.gwana.server.dto.token.RefreshTokenRequest;
import com.gwana.server.dto.token.TokenResponse;
import com.gwana.server.dto.user.*;
import com.gwana.server.service.AuthService;
import com.gwana.server.service.TokenService;
import com.gwana.server.service.UserService;
import io.hypersistence.tsid.TSID;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {
	private final UserService userService;
	private final TokenService tokenService;
	private final AuthService authService;
	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	private final TokenCookieManager tokenCookieManager;

	@Value("${spring.security.oauth2.client.registration.kakao.client-id}")
	private String kakaoClientId;

	@Value("${app.kakao.logout-uri}")
	private String kakaoLogoutUri;

	@Value("${app.kakao.logout-redirect-uri}")
	private String kakaoLogoutRedirectUri;

	@PostMapping("/signup")
	public ApiResponse<UserResponse> signupUser(@RequestBody UserSignupRequest userSignupRequest) {
		UserDto user = userService.createUser(userSignupRequest);

		UserResponse userResponse = UserResponse.builder()
				.userId(user.getUserId())
				.username(user.getUsername())
				.email(user.getEmail())
				.phone(user.getPhone())
				.role(user.getRole())
				.build();

		return ApiResponse.ok(userResponse);
	}

	@PostMapping("/signin")
	public ApiResponse<String> signinUser(@RequestBody UserSigninRequest userSigninRequest) {
		String email = userSigninRequest.getEmail();
		String password = userSigninRequest.getPassword();

		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(email, password);
		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(usernamePasswordAuthenticationToken);
		AuthUser authUser = (AuthUser) authentication.getPrincipal();
		String userId = authUser.getUserId();

		TokenResponse tokenResponse = tokenService.insertToken(userId, "");

		return ApiResponse.ok(tokenResponse.getAccessToken());
	}

	@PostMapping("/refresh/token")
	public ApiResponse<LoginResponse> refreshToken(
			@RequestBody RefreshTokenRequest refreshTokenRequest,
			@CookieValue(name = "refreshToken", required = false) String refreshTokenFromCookie,
			HttpServletResponse httpServletResponse
	) {
		String accessToken = refreshTokenRequest.getAccessToken();

		TokenResponse newToken = tokenService.refreshToken(accessToken, refreshTokenFromCookie);
		String userId = newToken.getUserId();
		tokenCookieManager.setRefreshTokenCookie(httpServletResponse, newToken.getRefreshToken());
		SocialUser socialUser = userService.findUserByUserId(userId);

		return ApiResponse.ok(LoginResponse.builder()
				.accessToken(newToken.getAccessToken())
				.loginType(socialUser.getProvider())
				.userId(userId)
				.customerKey(socialUser.getCustomerKey())
				.username(socialUser.getUsername())
				.email(socialUser.getEmail())
				.phone(socialUser.getPhone())
				.build());
	}

	@PostMapping("/callback")
	public ApiResponse<LoginResponse> kakaoLoginCallback(@RequestBody Map<String, String> request, HttpServletResponse httpServletResponse) {
		// 1. Kakao Social Login을 성공해 Authorization Server로 부터 Code를 전달 받는다.
		String code = request.get("code");
		String accessTokenFromKakao = tokenService.getAccessTokenByCode(code);
		UserFromKakao userFromKakao = tokenService.findUserFromKakao(accessTokenFromKakao);

		Long providerId = userFromKakao.getProviderId();
		String provider = userFromKakao.getProvider();

		String username = userFromKakao.getUsername();
		String email = userFromKakao.getEmail();
		String phone = userFromKakao.getPhone();

		UserSignupRequest userSignupRequest = UserSignupRequest.builder()
				.email(userFromKakao.getEmail())
				.username(userFromKakao.getUsername())
				.phone(userFromKakao.getPhone())
				.build();

		UserDto userDto = userService.createUserByKakao(userSignupRequest);
		String userId = userDto.getUserId();
		String customerKey = userDto.getCustomerKey();

		SocialAccountRequest socialAccountRequest = SocialAccountRequest.builder()
				.socialAccountId(TSID.Factory.getTsid().toString())
				.userId(userDto.getUserId())
				.providerId(providerId)
				.provider(provider)
				.authAccessToken(accessTokenFromKakao)
				.build();

		userService.createKakaoIfNoAccountInfo(socialAccountRequest);

		TokenResponse tokenResponse = tokenService.insertToken(userDto.getUserId(), accessTokenFromKakao);
		tokenCookieManager.setRefreshTokenCookie(httpServletResponse, tokenResponse.getRefreshToken());

		return ApiResponse.ok(LoginResponse.builder()
				.accessToken(tokenResponse.getAccessToken())
				.loginType(provider)
				.userId(userId)
				.customerKey(customerKey)
				.username(username)
				.email(email)
				.phone(phone)
				.build());
	}

	@GetMapping("/oauth2/logout/kakao")
	public void kakaoLogoutRedirect(HttpServletResponse response) {
		// 카카오 로그아웃으로 리다이렉트
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

	@PostMapping("/logout/kakao")
	public ApiResponse<Void> kakaoLogout(@RequestBody LogoutRequest logoutRequest) {
		authService.kakaoLogout(logoutRequest);

		return ApiResponse.ok(null);
	}
}
