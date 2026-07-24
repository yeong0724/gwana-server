package com.gwana.server.common.utils;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class TokenCookieManager {
    @Value("${app.cookie.secure}")
    private boolean cookieSecure;

    @Value("${app.cookie.max-age}")
    private int cookieMaxAge;

    @Value("${app.cookie.same-site}")
    private String cookieSameSite;

    private static final String REFRESH_TOKEN = "refreshToken";

    public void setRefreshTokenCookie(HttpServletResponse httpServletResponse, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN, refreshToken)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(cookieMaxAge)
                .sameSite(cookieSameSite)
                .build();

        httpServletResponse.addHeader("Set-Cookie", cookie.toString());
    }

    /**
     * 로그아웃 시 Refresh Token 쿠키를 즉시 만료(maxAge=0)시켜 제거한다.
     */
    public void clearRefreshTokenCookie(HttpServletResponse httpServletResponse) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(0)
                .sameSite(cookieSameSite)
                .build();

        httpServletResponse.addHeader("Set-Cookie", cookie.toString());
    }
}
