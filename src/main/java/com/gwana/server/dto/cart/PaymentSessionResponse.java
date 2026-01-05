package com.gwana.server.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentSessionResponse {
    private String productId;
    private String productName;
    private String categoryName;
    private int quantity;
    private int price;
    private int shippingPrice;
    private String[] images;
}
