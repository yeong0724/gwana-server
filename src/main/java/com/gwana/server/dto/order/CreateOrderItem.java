package com.gwana.server.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderItem {
    private String orderId;
    private Long productId;
    private String productName;
    private String productThumbnailUrl;
    private String categoryName;
    private Long productOptionId;
    private String optionName;
    private int optionPrice;
    private int quantity;
    private boolean isRequired;
}
