package com.gwana.server.controller;

import com.gwana.server.common.utils.ApiResponse;
import com.gwana.server.common.utils.TokenCookieManager;
import com.gwana.server.dto.token.TokenResponse;
import com.gwana.server.dto.user.*;
import com.gwana.server.service.TokenService;
import com.gwana.server.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
@Tag(name = "회원", description = "로컬 회원가입 / 로그인")
public class UserController {
	private final UserService userService;
	private final TokenService tokenService;
	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	private final TokenCookieManager tokenCookieManager;

	@Operation(summary = "회원가입", description = "로컬 계정 회원가입")
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

	@Operation(summary = "로그인", description = "이메일/비밀번호 로그인. Access Token 반환 + Refresh Token 쿠키 설정")
	@PostMapping("/signin")
	public ApiResponse<String> signinUser(
			@RequestBody UserSigninRequest userSigninRequest,
			HttpServletResponse httpServletResponse
	) {
		String email = userSigninRequest.getEmail();
		String password = userSigninRequest.getPassword();

		UsernamePasswordAuthenticationToken authenticationToken =
				new UsernamePasswordAuthenticationToken(email, password);
		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
		AuthUser authUser = (AuthUser) authentication.getPrincipal();

		TokenResponse tokenResponse = tokenService.issueTokens(authUser.getUserId());
		tokenCookieManager.setRefreshTokenCookie(httpServletResponse, tokenResponse.getRefreshToken());

		return ApiResponse.ok(tokenResponse.getAccessToken());
	}
}
