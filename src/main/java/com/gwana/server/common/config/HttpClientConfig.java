package com.gwana.server.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gwana.server.client.KakaoApiClient;
import com.gwana.server.client.KakaoAuthClient;
import com.gwana.server.client.TossPaymentApi;
import com.gwana.server.common.exception.CustomException;
import com.gwana.server.dto.payment.TossErrorResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 선언형 HTTP Interface(@HttpExchange) 클라이언트 빈 구성.
 * <p>
 * 손으로 작성하던 RestClient 래퍼 대신, {@code RestClientAdapter} + {@code HttpServiceProxyFactory}
 * 로 인터페이스 프록시를 생성한다(Spring 6 방식). 호스트별로 baseUrl 이 다르므로 클라이언트별로 구성한다.
 */
@Configuration
@EnableConfigurationProperties(KakaoProperties.class)
public class HttpClientConfig {

    @Bean
    public KakaoAuthClient kakaoAuthClient(
            @Value("${kakao.auth-base-url:https://kauth.kakao.com}") String baseUrl
    ) {
        return createClient(RestClient.builder().baseUrl(baseUrl).build(), KakaoAuthClient.class);
    }

    @Bean
    public KakaoApiClient kakaoApiClient(
            @Value("${kakao.api-base-url:https://kapi.kakao.com}") String baseUrl
    ) {
        return createClient(RestClient.builder().baseUrl(baseUrl).build(), KakaoApiClient.class);
    }

    @Bean
    public TossPaymentApi tossPaymentApi(
            @Value("${toss.secret-key}") String secretKey,
            @Value("${toss.api-url:https://api.tosspayments.com}") String apiUrl,
            ObjectMapper objectMapper
    ) {
        String basicAuth = "Basic " + Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

        RestClient restClient = RestClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, basicAuth)
                .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                    TossErrorResponse error = objectMapper.readValue(
                            response.getBody().readAllBytes(), TossErrorResponse.class);
                    throw new CustomException(error.getCode(), error.getMessage());
                })
                .build();

        return createClient(restClient, TossPaymentApi.class);
    }

    private <T> T createClient(RestClient restClient, Class<T> clientType) {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build();
        return factory.createClient(clientType);
    }
}
