package com.gwana.server.service;

import com.gwana.server.dto.product.*;
import com.gwana.server.mapper.ProductMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductMapper productMapper;

    public ProductService(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    public List<Product> getProductList(ProductListRequest productListRequest) {
        return productMapper.selectProductList(productListRequest);
    }

    public ProductDetailResponse getProduct(ProductRequest productRequest) {
        Product product = productMapper.selectProduct(productRequest);
        List<ProductOption> options = productMapper.selectProductOption(productRequest);

        return ProductDetailResponse.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .categoryId(product.getCategoryId())
                .categoryName(product.getCategoryName())
                .images(product.getImages())
                .infos(product.getInfos())
                .price(product.getPrice())
                .shippingPrice(product.getShippingPrice())
                .optionRequired(product.isOptionRequired())
                .options(options)
                .build();
    }
}
