package com.gwana.server.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 상품(SPU). 목록/내부 조회용. 가격은 갖지 않고 진열용 최저가(displayPrice)만 캐시한다.
 * 대표 썸네일(thumbnailUrl)은 갤러리 첫 이미지(gallery[0]).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private Long productId;
    private String name;
    private Long categoryId;
    private String categorySlug;
    private String categoryName;
    private String summary;
    private String detailContent;
    private String status;
    private Integer displayPrice;
    private Integer shippingPrice;
    private String thumbnailUrl;   // gallery[0]
    private BigDecimal avgRating;
    private int reviewCount;
}
