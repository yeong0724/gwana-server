package com.gwana.server.mapper;

import com.gwana.server.dto.product.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper {
    List<Product> selectProductList(ProductListRequest productListRequest);

    Product selectProduct(@Param("productId")Long productId);

    ProductOption selectProductOption(@Param("productOptionId")Long productOptionId);

    List<ProductOption> selectProductOptions(@Param("productId")Long productId);

    void insertProduct(ProductUpdateRequest productUpdateRequest);

    void updateProduct(ProductUpdateRequest productUpdateRequest);

    void insertProductOption(ProductOption productOption);

    void updateProductOption(ProductOption productOption);

    void deleteProductOption(ProductOptionDeleteRequest productOption);
}
