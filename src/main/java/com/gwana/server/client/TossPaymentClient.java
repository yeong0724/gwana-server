package com.gwana.server.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gwana.server.common.exception.CustomException;
import com.gwana.server.dto.payment.TossErrorResponse;
import com.gwana.server.dto.payment.TossPaymentRequest;
import com.gwana.server.dto.payment.TossPaymentResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.Base64;

@Slf4j
@Component
public class TossPaymentClient {

    private final RestClient restClient;
    private final String secretKey;

    public TossPaymentClient(
            @Value("${toss.secret-key}") String secretKey,
            @Value("${toss.api-url:https://api.tosspayments.com}") String apiUrl
    ) {
        this.secretKey = secretKey;
        this.restClient = RestClient.builder()
                .baseUrl(apiUrl)
                .build();
    }

    /**
     * READY: 결제를 생성하면 가지게 되는 초기 상태입니다. 인증 전까지는 READY 상태를 유지합니다.
     * IN_PROGRESS: 결제수단 정보와 해당 결제수단의 소유자가 맞는지 인증을 마친 상태입니다. 결제 승인 API를 호출하면 결제가 완료됩니다.
     * WAITING_FOR_DEPOSIT: 가상계좌 결제 흐름에만 있는 상태입니다. 발급된 가상계좌에 구매자가 아직 입금하지 않은 상태입니다.
     * DONE: 인증된 결제수단으로 요청한 결제가 승인된 상태입니다.
     * CANCELED: 승인된 결제가 취소된 상태입니다.
     * PARTIAL_CANCELED: 승인된 결제가 부분 취소된 상태입니다.
     * ABORTED: 결제 승인이 실패한 상태입니다.
     * EXPIRED: 결제 유효 시간 30분이 지나 거래가 취소된 상태입니다. IN_PROGRESS 상태에서 결제 승인 API를 호출하지 않으면 EXPIRED가 됩니다.
     */
    @SneakyThrows
    public TossPaymentResponse confirmPayment(TossPaymentRequest request) {
        String authorization = "Basic " + Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes());

        try {
            return restClient.post()
                    .uri("/v1/payments/confirm")
                    .header("Authorization", authorization)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(TossPaymentResponse.class);
        } catch (RestClientResponseException exception) {

            TossErrorResponse error = new ObjectMapper()
                    .readValue(exception.getResponseBodyAsString(), TossErrorResponse.class);
            throw new CustomException(error.getCode(), error.getMessage());
        }
    }
}