package com.gwana.server.client;

import com.gwana.server.dto.user.UserFromKakao;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class KakaoUserHttpClient {
    @Value("${spring.security.oauth2.kakao.userinfo-api-url}")
    private String KAKAO_USERINFO_API_URL;

    @Value("${app.kakao.logout-token-uri}")
    private String KAKAO_LOGOUT_TOKEN_URI;

    public UserFromKakao findUserFromKakao(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);  // 액세스 토큰을 Authorization Header에 추가

        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                KAKAO_USERINFO_API_URL,
                HttpMethod.GET,
                httpEntity,
                Map.class
        );

        Long providerId = (Long) response.getBody().get("id");

        Map properties = (Map) response.getBody().get("kakao_account");

        String username = (String) properties.get("name");
        String email = (String) properties.get("email");
        String phoneNumber = (String) properties.get("phone_number");

        return UserFromKakao.builder()
                .username(username)
                .email(email)
                .phone(phoneNumber)
                .provider("kakao")
                .providerId(providerId)
                .build();
    }

    public void kakaoLogout(String authAccessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + authAccessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        restTemplate.exchange(KAKAO_LOGOUT_TOKEN_URI, HttpMethod.POST, request, String.class);
    }
}
