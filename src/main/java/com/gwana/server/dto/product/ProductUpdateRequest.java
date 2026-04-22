package com.gwana.server.dto.product;

import com.gwana.server.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProductUpdateRequest extends BaseDto {
    private Long productId;
    private String productName;
    private String categoryId;
    private String categoryName;
    private String[] images;
    private String[] infos;
    private Integer price;
    private Integer shippingPrice;
    private List<ProductOption> options;
}
