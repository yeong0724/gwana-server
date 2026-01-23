package com.gwana.server.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartOption {
    private Long cartId;
    private String optionId;
    private String optionName;
    private int optionPrice;
    private int quantity;
}
