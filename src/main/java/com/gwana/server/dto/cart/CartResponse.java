package com.gwana.server.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartResponse {
    private Long cartId;
    private String productId;
    private int quantity;
    private String productName;
    private String categoryName;
    private int price;
    private int shippingPrice;
    private String[] images;
}
