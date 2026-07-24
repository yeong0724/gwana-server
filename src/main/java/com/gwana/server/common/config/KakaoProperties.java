package com.gwana.server.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 카카오 OAuth 클라이언트 자격증명. 표준 스프링 OAuth2 등록 프로퍼티에 바인딩된다.
 * (client-id → clientId 등 relaxed binding)
 */
@ConfigurationProperties(prefix = "spring.security.oauth2.client.registration.kakao")
public record KakaoProperties(
        String clientId,
        String clientSecret,
        String redirectUri
) {
}
