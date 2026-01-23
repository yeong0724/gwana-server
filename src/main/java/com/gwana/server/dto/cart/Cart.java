package com.gwana.server.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cart {
    private Long cartId;
    private String productId;
    private int quantity;
    private String productName;
    private String categoryName;
    private int price;
    private int shippingPrice;
    private String[] images;
    private boolean optionRequired;
    private String optionId;
    private String optionName;
    private int optionPrice;;
}
