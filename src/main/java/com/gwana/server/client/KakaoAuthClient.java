package com.gwana.server.client;

import com.gwana.server.dto.kakao.KakaoTokenResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

/**
 * 카카오 인증 서버(kauth.kakao.com) 선언형 HTTP 클라이언트.
 * <p>
 * Spring 6 HTTP Interface(@HttpExchange) 기반. 구현체는 {@code HttpServiceProxyFactory}
 * 가 런타임에 생성한다(HttpClientConfig 참고).
 */
@HttpExchange
public interface KakaoAuthClient {

    /**
     * 인가 코드로 토큰을 발급받는다. 폼(x-www-form-urlencoded) 파라미터는 요청 본문으로 인코딩된다.
     */
    @PostExchange(url = "/oauth/token", contentType = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    KakaoTokenResponse issueToken(
            @RequestParam("grant_type") String grantType,
            @RequestParam("client_id") String clientId,
            @RequestParam("client_secret") String clientSecret,
            @RequestParam("redirect_uri") String redirectUri,
            @RequestParam("code") String code
    );
}
