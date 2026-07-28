package com.gwana.server.service;

import com.gwana.server.client.S3UploadClient;
import com.gwana.server.common.security.JwtTokenProvider;
import com.gwana.server.common.utils.Validate;
import com.gwana.server.dto.AuthAware;
import com.gwana.server.dto.InfiniteResponse;
import com.gwana.server.dto.mypage.Inquiry;
import com.gwana.server.dto.mypage.ProductInquiryListSearchRequest;
import com.gwana.server.dto.product.*;
import com.gwana.server.dto.user.AuthUser;
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
    private final JwtTokenProvider jwtTokenProvider;

    public List<Product> getProductList(ProductListRequest productListRequest) {
        return productMapper.selectProductList(productListRequest);
    }

    public Product getProduct(Long productId) {
        return productMapper.selectProduct(productId);
    }

    public ProductDetailResponse getProductDetail(ProductRequest productRequest) {
        Long productId = productRequest.getProductId();

        Product product = this.getProduct(productId);

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

    // 업로드 제한은 서버에서 고정 (클라이언트가 조정 불가)
    private static final long PRODUCT_IMAGE_MAX_SIZE = 3 * 1024 * 1024;

    public String uploadProductImage(MultipartFile multipartFile, String folderPath) {
        String contentType = Validate.validateFile(multipartFile, PRODUCT_IMAGE_MAX_SIZE);
        String safeFolder = Validate.validateFolderPath(folderPath);

        return s3UploadClient.uploadImage(multipartFile, safeFolder, contentType);
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

    public ProductOption getProductOption(Long productOptionId) {
        return productMapper.selectProductOption(productOptionId);
    }

    public InfiniteResponse<List<Inquiry>> productsInquiryListSearch(ProductInquiryListSearchRequest productInquiryListSearchRequest) {
        injectAuthInfo(productInquiryListSearchRequest);

        int page = productInquiryListSearchRequest.getPage();
        int size = productInquiryListSearchRequest.getSize();
        int offset = page * size;
        productInquiryListSearchRequest.setOffset(offset);


        long totalCount = productMapper.selectProductInquiryCount(productInquiryListSearchRequest);
        boolean hasNext = (long) (page + 1) * size < totalCount;
        List<Inquiry> list = productMapper.selectProductInquiryList(productInquiryListSearchRequest);

        list.forEach(Inquiry::mask);

        return InfiniteResponse.<List<Inquiry>>builder()
                .data(list)
                .page(page)
                .size(size)
                .totalCount(totalCount)
                .hasNext(hasNext)
                .build();
    }

    private void injectAuthInfo(AuthAware request) {
        AuthUser authUser = jwtTokenProvider.getUserInfo();
        if (authUser != null) {
            request.setUserId(authUser.getUserId());
            request.setRole(jwtTokenProvider.getRole());
        }
    }
}
