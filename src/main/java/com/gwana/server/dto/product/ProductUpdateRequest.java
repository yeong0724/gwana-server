package com.gwana.server.dto.product;

import com.gwana.server.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * 상품 등록/수정 요청.
 * 이미지는 갤러리[]/디테일[] URL 리스트 + variant별 썸네일(변형 안에 포함).
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProductUpdateRequest extends BaseDto {
    private Long productId;
    private Long categoryId;
    private String name;
    private String summary;
    private String detailContent;
    private String status;
    private Integer shippingPrice;

    private List<VariantUpsertRequest> variants;
    private List<String> galleryUrls;
    private List<String> detailUrls;
    private List<Long> addonIds;
}
