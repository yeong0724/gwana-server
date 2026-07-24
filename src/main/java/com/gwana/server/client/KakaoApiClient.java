package com.gwana.server.client;

import com.gwana.server.dto.kakao.KakaoUserResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

/**
 * 카카오 API 서버(kapi.kakao.com) 선언형 HTTP 클라이언트.
 */
@HttpExchange
public interface KakaoApiClient {

    /** 사용자 정보 조회 */
    @GetExchange("/v2/user/me")
    KakaoUserResponse getUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken);

    /** 카카오 로그아웃(해당 서비스의 토큰 만료) */
    @PostExchange("/v1/user/logout")
    void logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken);
}
