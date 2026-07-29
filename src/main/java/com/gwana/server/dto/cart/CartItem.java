package com.gwana.server.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {
    private Long cartItemId;
    private Long cartId;
    private int quantity;
    private Long productVariantId;
    private Long productId;
    private String optionLabel;
    private int price;
    private String status;      // variant 판매상태
}
