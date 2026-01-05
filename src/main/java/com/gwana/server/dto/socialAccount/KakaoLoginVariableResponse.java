package com.gwana.server.dto.socialAccount;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@RequiredArgsConstructor
@AllArgsConstructor
public class KakaoLoginVariableResponse {
    private String kakaoClientId;
    private String kakaoRedirectUri;
    private String kakaoInfoScope;
}
