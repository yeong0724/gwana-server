package com.gwana.server.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TossPaymentRequest {
    private String paymentKey;
    private String orderId;
    private int amount;
}