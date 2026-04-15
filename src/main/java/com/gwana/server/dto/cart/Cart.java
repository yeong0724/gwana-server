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
    private String productName;
    private String categoryId;
    private String categoryName;
    private int price;
    private int shippingPrice;
    private String[] images;
    private List<CartItem> cartItems;
}
