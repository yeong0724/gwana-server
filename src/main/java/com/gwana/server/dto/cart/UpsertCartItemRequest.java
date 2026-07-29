package com.gwana.server.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpsertCartItemRequest {
    private Long productVariantId;
    private Long cartId;
    private int quantity;
}
