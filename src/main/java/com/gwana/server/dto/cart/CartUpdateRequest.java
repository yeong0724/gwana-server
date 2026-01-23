package com.gwana.server.dto.cart;

import com.gwana.server.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CartUpdateRequest extends BaseDto {
    private Long cartId;
    private String productId;
    private String optionId;
    private int quantity;
    private String userId;
}
