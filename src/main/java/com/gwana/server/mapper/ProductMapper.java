package com.gwana.server.mapper;

import com.gwana.server.dto.product.ProductListRequest;
import com.gwana.server.dto.product.Product;
import com.gwana.server.dto.product.ProductOption;
import com.gwana.server.dto.product.ProductRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductMapper {
    List<Product> selectProductList(ProductListRequest productListRequest);

    Product selectProduct(ProductRequest productRequest);

    List<ProductOption> selectProductOption(ProductRequest productRequest);
}
