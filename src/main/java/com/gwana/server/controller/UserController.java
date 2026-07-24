package com.gwana.server.controller;

import com.gwana.server.common.utils.ApiResponse;
import com.gwana.server.common.utils.TokenCookieManager;
import com.gwana.server.dto.token.TokenResponse;
import com.gwana.server.dto.user.*;
import com.gwana.server.service.TokenService;
import com.gwana.server.service.UserService;
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
public class UserController {
	private final UserService userService;
	private final TokenService tokenService;
	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	private final TokenCookieManager tokenCookieManager;

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
