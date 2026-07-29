package com.gwana.server.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 추가상품(애드온). 본품에 선택적으로 얹는 유료 부가구성. price 는 추가금.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductAddon {
    private Long productAddonId;
    private String name;
    private Integer price;
}
