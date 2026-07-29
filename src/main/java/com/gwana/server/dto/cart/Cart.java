package com.gwana.server.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cart {
    private Long cartId;
    private Long productId;
    private String name;
    private Long categoryId;
    private String categoryName;
    private String thumbnailUrl;   // gallery[0]
    private int shippingPrice;
    private List<CartItem> cartItems;
}
