package com.gwana.server.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderOption {
    private Long productOptionId;
    private String optionName;
    private int optionPrice;
    private int quantity;
    private Boolean isRequired;
}
