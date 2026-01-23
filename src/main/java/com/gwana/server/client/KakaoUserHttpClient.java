package com.gwana.server.client;

import com.gwana.server.dto.user.UserFromKakao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class KakaoUserHttpClient {

    private final RestClient restClient;
    private final String userInfoUrl;
    private final String logoutUrl;

    public KakaoUserHttpClient(
            @Value("${spring.security.oauth2.kakao.userinfo-api-url}") String userInfoUrl,
            @Value("${app.kakao.logout-token-uri}") String logoutUrl
    ) {
        this.userInfoUrl = userInfoUrl;
        this.logoutUrl = logoutUrl;
        this.restClient = RestClient.create();
    }

    public UserFromKakao findUserFromKakao(String accessToken) {
        Map<String, Object> response = restClient.get()
                .uri(userInfoUrl)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        Long providerId = ((Number) response.get("id")).longValue();
        Map<String, Object> kakaoAccount = (Map<String, Object>) response.get("kakao_account");

        String phoneNumber = (String) kakaoAccount.get("phone_number");
        return UserFromKakao.builder()
                .username((String) kakaoAccount.get("name"))
                .email((String) kakaoAccount.get("email"))
                .phone(getPhoneNumber(phoneNumber))
                .provider("kakao")
                .providerId(providerId)
                .build();
    }

    public void kakaoLogout(String accessToken) {
        restClient.post()
                .uri(logoutUrl)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .toBodilessEntity();
    }

    public String getPhoneNumber(String phone) {
        if (phone == null) return "";

        // 숫자만 추출
        String digits = phone.replaceAll("[^0-9]", "");

        // +82로 시작하면 0으로 변환
        if (digits.startsWith("82")) {
            digits = "0" + digits.substring(2);
        }

        return digits; // 01012345678 형태
    }
}
