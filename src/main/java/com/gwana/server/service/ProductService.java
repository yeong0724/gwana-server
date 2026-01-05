package com.gwana.server.service;

import com.gwana.server.dto.product.ProductListRequest;
import com.gwana.server.dto.product.ProductListResponse;
import com.gwana.server.dto.product.ProductRequest;
import com.gwana.server.mapper.ProductMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductMapper productMapper;

    public ProductService(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    public List<ProductListResponse> getProductList(ProductListRequest productListRequest) {
        return productMapper.selectProductList(productListRequest);
    }

    public ProductListResponse getProduct(ProductRequest productRequest) {
        return productMapper.selectProduct(productRequest);
    }
}
