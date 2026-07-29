package com.gwana.server.dto.product;

import lombok.Data;

/**
 * 애드온(추가상품) 삭제 요청. 소프트 삭제(deleted_at 세팅 → is_deleted=1)로 처리된다.
 */
@Data
public class ProductAddonDeleteRequest {
    private Long productAddonId;
}
