package com.gwana.server.service;

import com.gwana.server.client.S3UploadClient;
import com.gwana.server.common.utils.Validate;
import com.gwana.server.dto.product.*;
import com.gwana.server.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    private final ProductMapper productMapper;
    private final S3UploadClient s3UploadClient;

    public List<Product> getProductList(ProductListRequest productListRequest) {
        return productMapper.selectProductList(productListRequest);
    }

    public ProductDetailResponse getProduct(ProductRequest productRequest) {
        Product product = productMapper.selectProduct(productRequest);

        Long productId = product.getProductId();
        List<ProductOption> options = productMapper.selectProductOptions(productId);

        return ProductDetailResponse.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .categoryId(product.getCategoryId())
                .categoryName(product.getCategoryName())
                .images(product.getImages())
                .infos(product.getInfos())
                .price(product.getPrice())
                .shippingPrice(product.getShippingPrice())
                .options(options)
                .build();
    }

    public String uploadProductImage(MultipartFile multipartFile, String folderPath, long maxFileSize) {
        long MAX_FILE_SIZE = maxFileSize * 1024 * 1024;
        Validate.validateFile(multipartFile, MAX_FILE_SIZE);

        return s3UploadClient.uploadImage(multipartFile, folderPath);
    }

    public void createProduct(ProductUpdateRequest productUpdateRequest) {
        productMapper.insertProduct(productUpdateRequest);
    }

    public void updateProduct(ProductUpdateRequest productUpdateRequest) {
        productMapper.updateProduct(productUpdateRequest);

        List<ProductOption> productOptions = productUpdateRequest.getOptions();

        for (ProductOption productOption : productOptions) {
            Long productOptionId = productOption.getProductOptionId();

            if (productOptionId == null) {
                productMapper.insertProductOption(productOption);
            } else {
                productMapper.updateProductOption(productOption);
            }
        }
    }

    public void deleteProductImage(String imageUrl) {
        s3UploadClient.deleteImage(imageUrl);
    }

    public void deleteProductOption(ProductOptionDeleteRequest productOptionDeleteRequest) {
        productMapper.deleteProductOption(productOptionDeleteRequest);
    }
}
