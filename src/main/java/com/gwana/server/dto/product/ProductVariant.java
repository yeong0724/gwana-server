package com.gwana.server.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 판매 단위(SKU). 가격의 단일 진실원천.
 * 단일 축 옵션이라 option_label 로 표기하고 관리자가 직접 입력한다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant {
    private Long productVariantId;
    private Long productId;
    private String optionLabel;
    private Integer price;
    private String status;        // ON_SALE / SOLD_OUT / HIDDEN
    private Integer sortOrder;
    private String thumbnailUrl;  // product_image(THUMBNAIL) 조인 결과
}
