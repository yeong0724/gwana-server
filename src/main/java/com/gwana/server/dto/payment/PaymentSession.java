package com.gwana.server.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentSession {
    private String productId;
    private String productName;
    private String categoryName;
    private int quantity;
    private int price;
    private int shippingPrice;
    private String[] images;
}
