package com.gwana.server.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VariantUpsertRequest {
    private Long productVariantId;   // null 이면 신규
    private String optionLabel;
    private Integer price;
    private String status;
    private Integer sortOrder;
    private String thumbnailUrl;     // variant 썸네일(선택)
}
