package com.gwana.server.dto.cart;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    private Long productOptionId;
    private Long productId;
    private String optionName;
    private int optionPrice;

    @JsonProperty("isRequired")
    private boolean isRequired;

    @JsonProperty("isQuantityAdjustable")
    private boolean isQuantityAdjustable;
}
