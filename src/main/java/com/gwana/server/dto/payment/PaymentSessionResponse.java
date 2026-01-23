package com.gwana.server.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSessionResponse {
    private List<PaymentSession> items;
    private int totalPrice;
    private int totalShippingPrice;
    private String sessionId;
}
