package com.gwana.server.service;

import com.gwana.server.client.TossPaymentClient;
import com.gwana.server.common.enums.ErrorCode;
import com.gwana.server.common.exception.CommonException;
import com.gwana.server.common.security.JwtTokenProvider;
import com.gwana.server.dto.payment.*;
import com.gwana.server.dto.user.AuthUser;
import com.gwana.server.mapper.PaymentMapper;
import io.hypersistence.tsid.TSID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentService {
    private final JwtTokenProvider jwtTokenProvider;
    private final PaymentMapper paymentMapper;
    private final TossPaymentClient tossPaymentClient;

    public String createPaymentSession(List<PaymentSessionRequest> paymentSessionRequestList) {
        AuthUser authUser = jwtTokenProvider.getUserInfo();
        String userId = authUser.getUserId();

        paymentMapper.deletePaymentSession(userId);

        String sessionId = TSID.Factory.getTsid().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(30);

        for (PaymentSessionRequest paymentSessionRequest : paymentSessionRequestList) {
            paymentSessionRequest.setUserId(userId);
            paymentSessionRequest.setSessionId(sessionId);
            paymentSessionRequest.setExpiresAt(expiresAt);

            paymentMapper.createPaymentSession(paymentSessionRequest);
        }

        return sessionId;
    }

    public PaymentSessionResponse getPaymentSession(String sessionId) {
        List<PaymentSession> PaymentSessionList = paymentMapper.getPaymentSession(sessionId);

        int totalPrice = 0;
        int totalShippingPrice = 0;
        for (PaymentSession paymentSession : PaymentSessionList) {
            int price = paymentSession.getPrice();
            int quantity = paymentSession.getQuantity();
            int shippingPrice = paymentSession.getShippingPrice();

            totalPrice += price * quantity;
            totalShippingPrice += shippingPrice;
        }

        // 합계 주문 금액이 50,000원 이상시 배송비 무료
        if (totalPrice >= 50000) {
            totalShippingPrice = 0;
        }

        return PaymentSessionResponse.builder()
                .items(PaymentSessionList)
                .totalPrice(totalPrice)
                .totalShippingPrice(totalShippingPrice)
                .sessionId(sessionId)
                .build();
    }

    public void savePaymentInfo(PaymentInfoRequest paymentInfoRequest) {
        // 로그인 ID
        AuthUser authUser = jwtTokenProvider.getUserInfo();
        String userId = authUser.getUserId();
        paymentInfoRequest.setUserId(userId);

        // 만료시간
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(30);
        paymentInfoRequest.setExpiresAt(expiresAt);

        int totalAmount = paymentInfoRequest.getTotalPrice() + paymentInfoRequest.getTotalShippingPrice();
        paymentInfoRequest.setTotalAmount(totalAmount);

        paymentMapper.upsertPaymentInfo(paymentInfoRequest);
    }

    public TossPaymentResponse paymentVerification(TossPaymentRequest tossPaymentRequest) {
        String paymentKey = tossPaymentRequest.getPaymentKey();
        String orderId = tossPaymentRequest.getOrderId();
        int amount = tossPaymentRequest.getAmount();

        PaymentInfoResponse paymentInfoResponse = paymentMapper.findByOrderId(orderId);

        if (paymentInfoResponse == null) {
            throw new CommonException(ErrorCode.NOT_MATCHED_AMOUNT);
        }

        TossPaymentRequest request = TossPaymentRequest.builder()
                .paymentKey(paymentKey)
                .orderId(orderId)
                .amount(amount)
                .build();

        return tossPaymentClient.confirmPayment(request);
    }
}
