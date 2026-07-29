package com.gwana.server.dto.product;

import lombok.Data;

/**
 * 관리자 상품 상태 변경 요청. status 는 product_status enum 문자열.
 * (ON_SALE / SOLD_OUT / HIDDEN / DISCONTINUED)
 */
@Data
public class ProductStatusUpdateRequest {
    private Long productId;
    private String status;
}
