package com.gwana.server.dto.cart;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemUpdateRequest {
    private Long cartItemId;
    private int quantity;
}
