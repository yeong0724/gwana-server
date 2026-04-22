
package com.gwana.server.common.security;

import com.gwana.server.dto.user.AuthUser;
import com.gwana.server.dto.user.SocialUser;
import com.gwana.server.service.TokenService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Http 요청에서 jwt Token을 추출하고 인증 정보를 설정하는 Filter Class
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final TokenService tokenService;
	private final HandlerExceptionResolver handlerExceptionResolver;

	/**
	 * OPTIONS 요청은 Filter를 건너뛰도록 설정 (CORS 사전 요청 - Preflight Request 등 무시)
	 * - Preflight Request: 클라이언트가 실제 요청전에 옵션 요청을 보내서 서버가 이 요청을 허용할지 확인하는 과정
	 * - 이 사전 요청에 대해서는 jwt 인증 절차를 굳이 거칠 필요는 없음
	 */
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getRequestURI();
		return request.getMethod().equals("OPTIONS")
				|| path.startsWith("/user/")
				|| path.startsWith("/auth/")
				|| path.startsWith("/product/")
				|| path.equals("/mypage/search/review/list");
	}

	/**
	 * 1. Request 에서 jwt 출출후 파싱
	 * 2. 파싱한 Token 검증
	 * 3. 인증 객체 생성
	 * 4. SecurityContext 설정
	 */
	@Override
	protected void doFilterInternal(
			HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
	) throws ServletException, IOException {
		String accessToken = resolveToken(request);
		try {
			if (accessToken != null && tokenService.validateToken(accessToken)) {
				SocialUser socialUser = tokenService.findUserByAccessToken(accessToken);
				Authentication authentication = getAuthentication(socialUser);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}

			filterChain.doFilter(request, response);
		} catch (MalformedJwtException | ExpiredJwtException exception) {
			handlerExceptionResolver.resolveException(request, response, null, exception);
		}
    }

	@NonNull
	private static Authentication getAuthentication(SocialUser socialUser) {
		List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + socialUser.getRole().name()));
		String password = Optional.ofNullable(socialUser.getPassword()).orElse("password");
		AuthUser principal = new AuthUser(
				socialUser.getUserId(),
				socialUser.getUsername(),
				password,
				socialUser.getEmail(),
				socialUser.getPhone(),
				authorities
		);
		return new UsernamePasswordAuthenticationToken(principal, socialUser.getUserId(), authorities);
	}

	private String resolveToken(HttpServletRequest httpServletRequest) {
		String bearerToken = httpServletRequest.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
			return bearerToken.substring(7);
		}

		return null;
	}
}
