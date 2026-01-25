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

    public void setRefreshTokenCookie(HttpServletResponse httpServletResponse, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(cookieMaxAge)
                .sameSite(cookieSameSite)
                .build();

        httpServletResponse.addHeader("Set-Cookie", cookie.toString());
    }
}
