package com.gwana.server.service;

import com.gwana.server.client.KakaoUserHttpClient;
import com.gwana.server.dto.token.Token;
import com.gwana.server.dto.user.LogoutRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final TokenService tokenService;
    private final KakaoUserHttpClient kakaoUserHttpClient;

    @Transactional
    public void kakaoLogout(LogoutRequest logoutRequest) {
        String accessToken = logoutRequest.getAccessToken();
        Token token = tokenService.getTokenByAccessToken(accessToken);

        String authAccessToken = token.getAuthAccessToken();

        kakaoUserHttpClient.kakaoLogout(authAccessToken);
        tokenService.deleteTokenByAccessToken(authAccessToken);
    }
}