package com.gwana.server.common.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenCookieManager {
    @Value("${app.cookie.secure}")
    private boolean cookieSecure;

    @Value("${app.cookie.max-age}")
    private int cookieMaxAge;

    public void setRefreshTokenCookie(HttpServletResponse httpServletResponse, String refreshToken) {
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(cookieSecure); // HTTPS 환경에서만 true
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(cookieMaxAge); // 하루
        httpServletResponse.addCookie(refreshTokenCookie);
    }
}
