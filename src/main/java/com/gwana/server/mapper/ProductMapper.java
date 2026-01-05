package com.gwana.server.mapper;

import com.gwana.server.dto.product.ProductListRequest;
import com.gwana.server.dto.product.ProductListResponse;
import com.gwana.server.dto.product.ProductRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductMapper {
    List<ProductListResponse> selectProductList(ProductListRequest productListRequest);

    ProductListResponse selectProduct(ProductRequest productRequest);
}
