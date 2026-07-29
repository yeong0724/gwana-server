package com.gwana.server.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 상품 이미지. 한 테이블에서 image_type 으로 갤러리/디테일/variant 썸네일을 구분.
 * - GALLERY / DETAIL : product_variant_id = null (상품 공통)
 * - THUMBNAIL        : product_variant_id 지정 (variant당 1건)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImage {
    private Long productImageId;
    private Long productId;
    private Long productVariantId;
    private String imageType;   // THUMBNAIL / GALLERY / DETAIL
    private String url;
    private String altText;
    private Integer sortOrder;
}
