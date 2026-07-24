package com.gwana.server.dto.kakao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 카카오 사용자 정보 응답 (GET https://kapi.kakao.com/v2/user/me).
 * 동의 항목 미동의 시 각 필드는 null 일 수 있다.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoUserResponse(
        Long id,
        @JsonProperty("kakao_account") KakaoAccount kakaoAccount
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record KakaoAccount(
            String name,
            String email,
            @JsonProperty("phone_number") String phoneNumber
    ) {
    }
}
