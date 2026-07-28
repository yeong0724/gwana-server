package com.gwana.server.controller;

import com.gwana.server.common.utils.ApiResponse;
import com.gwana.server.dto.payment.*;
import com.gwana.server.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "결제", description = "결제 세션 / 결제정보 저장 / 결제 승인 (인증 필요)")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {
    private final PaymentService paymentService;

    @Operation(summary = "결제 세션 생성")
    @PostMapping("/create/session")
    public ApiResponse<String> createPaymentSession(@RequestBody List<PaymentSessionRequest> paymentSessionRequestList) {
        String sessionId = paymentService.createPaymentSession(paymentSessionRequestList);

        return ApiResponse.ok(sessionId);
    }

    @Operation(summary = "결제 세션 조회")
    @PostMapping("/search/session")
    public ApiResponse<PaymentSessionResponse> createPaymentSession(@RequestBody PaymentSessionRequest paymentSessionRequest) {
        String sessionId = paymentSessionRequest.getSessionId();
        return ApiResponse.ok(paymentService.getPaymentSession(sessionId));
    }

    /**
     * 결제 정보 저장 API
     * - 주문자, 배송정보, 결제금액
     */
    @Operation(summary = "결제 정보 저장", description = "주문자·배송정보·결제금액 저장")
    @PostMapping("/save/info")
    public ApiResponse<Void> savePaymentInfo(@RequestBody PaymentInfoRequest paymentInfoRequest) {
        paymentService.savePaymentInfo(paymentInfoRequest);
        return ApiResponse.ok(null);
    }

    @Operation(summary = "결제 승인 요청", description = "토스페이먼츠 결제 승인")
    @PostMapping("/request/approve")
    public ApiResponse<TossPaymentResponse> requestApprove(@RequestBody TossPaymentRequest tossPaymentRequest) {
        return ApiResponse.ok(paymentService.paymentVerification(tossPaymentRequest));
    }
}
