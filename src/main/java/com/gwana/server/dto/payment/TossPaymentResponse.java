package com.gwana.server.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TossPaymentResponse {
    private String paymentKey;
    private String orderId;
    private String status;
    private int totalAmount;
    private String method;
    private String approvedAt;
    private Receipt receipt;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Receipt {
        private String url;
    }
}