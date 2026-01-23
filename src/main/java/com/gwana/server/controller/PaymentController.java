package com.gwana.server.controller;

import com.gwana.server.common.utils.ApiResponse;
import com.gwana.server.dto.payment.*;
import com.gwana.server.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/create/session")
    public ApiResponse<String> createPaymentSession(@RequestBody List<PaymentSessionRequest> paymentSessionRequestList) {
        String sessionId = paymentService.createPaymentSession(paymentSessionRequestList);

        return ApiResponse.ok(sessionId);
    }

    @PostMapping("/search/session")
    public ApiResponse<PaymentSessionResponse> createPaymentSession(@RequestBody PaymentSessionRequest paymentSessionRequest) {
        String sessionId = paymentSessionRequest.getSessionId();
        return ApiResponse.ok(paymentService.getPaymentSession(sessionId));
    }

    /**
     * 결제 정보 저장 API
     * - 주문자, 배송정보, 결제금액
     */
    @PostMapping("/save/info")
    public ApiResponse<Void> savePaymentInfo(@RequestBody PaymentInfoRequest paymentInfoRequest) {
        paymentService.savePaymentInfo(paymentInfoRequest);
        return ApiResponse.ok(null);
    }

    @PostMapping("/request/approve")
    public ApiResponse<TossPaymentResponse> requestApprove(@RequestBody TossPaymentRequest tossPaymentRequest) {
        return ApiResponse.ok(paymentService.paymentVerification(tossPaymentRequest));
    }
}
