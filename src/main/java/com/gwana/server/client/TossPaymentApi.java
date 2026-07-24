package com.gwana.server.client;

import com.gwana.server.dto.payment.TossPaymentRequest;
import com.gwana.server.dto.payment.TossPaymentResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

/**
 * 토스페이먼츠 결제 API 선언형 HTTP 클라이언트.
 * <p>
 * 인증 헤더(Basic)와 에러 응답 → CustomException 매핑은 HttpClientConfig 의
 * RestClient 설정(defaultHeader/defaultStatusHandler)에서 처리한다.
 */
@HttpExchange
public interface TossPaymentApi {

    /** 결제 승인 */
    @PostExchange(url = "/v1/payments/confirm", contentType = MediaType.APPLICATION_JSON_VALUE)
    TossPaymentResponse confirm(@RequestBody TossPaymentRequest request);
}
