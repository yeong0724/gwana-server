package com.gwana.server.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import java.util.Map;

@Component
public class KakaoTokenHttpClient {

    private final RestClient restClient;
    private final String kakaoClientId;
    private final String kakaoClientSecret;
    private final String kakaoRedirectUri;
    private final String tokenUrl;

    public KakaoTokenHttpClient(
            @Value("${spring.security.oauth2.client.registration.kakao.client-id}") String kakaoClientId,
            @Value("${spring.security.oauth2.client.registration.kakao.client-secret}") String kakaoClientSecret,
            @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}") String kakaoRedirectUri,
            @Value("${spring.security.oauth2.kakao.token-url}") String tokenUrl
    ) {
        this.kakaoClientId = kakaoClientId;
        this.kakaoClientSecret = kakaoClientSecret;
        this.kakaoRedirectUri = kakaoRedirectUri;
        this.tokenUrl = tokenUrl;
        this.restClient = RestClient.create();
    }

    public String getAccessTokenByCode(String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("client_secret", kakaoClientSecret);
        params.add("code", code);

        Map<String, Object> response = restClient.post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(params)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        return (String) response.get("access_token");
    }
}