package com.gwana.server.dto.cart;

import com.gwana.server.dto.BaseDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UpsertCartRequest extends BaseDto {
    private Long cartId;
    private Long productId;
    private String userId;
    private List<UpsertCartItemRequest> cartItems;
}
