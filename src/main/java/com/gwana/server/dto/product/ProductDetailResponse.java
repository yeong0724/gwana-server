package com.gwana.server.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailResponse {
    private String productId;
    private String productName;
    private String categoryId;
    private String categoryName;
    private String[] images;
    private String[] infos;
    private Integer price;
    private Integer shippingPrice;
    private boolean optionRequired;
    private List<ProductOption> options;
}
