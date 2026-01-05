package com.gwana.server.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentSessionRequest {
    private String productId;
    private String quantity;
    private String sessionId;
    private String userId;
    private LocalDateTime expiresAt;
}