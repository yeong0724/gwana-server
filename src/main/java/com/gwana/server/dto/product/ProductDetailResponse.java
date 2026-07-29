package com.gwana.server.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 상품 상세 응답.
 * 이미지는 갤러리[]/디테일[] (상품 공통)로 내리고, variant 썸네일은 각 variant 안에 담는다.
 * 옵션 셀렉트는 variants 를 그대로 나열(패턴2)한다.
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailResponse {
    private Long productId;
    private String name;
    private Long categoryId;
    private String categoryName;
    private String summary;
    private String detailContent;
    private String status;
    private Integer shippingPrice;
    private Integer displayPrice;

    private List<String> galleryImages;
    private List<String> detailImages;

    private List<ProductVariant> variants;
    private List<ProductAddon> addons;

    private BigDecimal avgRating;
    private int reviewCount;
}
