package com.gwana.server.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentInfoRequest {
    private String sessionId;
    private String orderId;

    private String userId;

    // 금액 정보
    private int totalPrice;
    private int totalShippingPrice;
    private int totalAmount;

    // 주문자 정보
    private String senderName;
    private String senderPhone;

    // 배송지 정보
    private String recipientName;
    private String recipientPhone;
    private String zonecode;
    private String roadAddress;
    private String detailAddress;
    private String deliveryRequest;
    private String deliveryRequestDetail;

    private LocalDateTime expiresAt;
}
