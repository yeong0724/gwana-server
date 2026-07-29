package com.gwana.server.mapper;

import com.gwana.server.dto.mypage.Inquiry;
import com.gwana.server.dto.mypage.ProductInquiryListSearchRequest;
import com.gwana.server.dto.product.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper {
    // 조회
    List<Product> selectProductList(ProductListRequest productListRequest);

    List<Product> selectAdminProductList(ProductListRequest productListRequest);

    void updateProductStatus(@Param("productId") Long productId, @Param("status") String status);

    Product selectProduct(@Param("productId") Long productId);

    ProductVariant selectProductVariant(@Param("productVariantId") Long productVariantId);

    List<ProductVariant> selectProductVariants(@Param("productId") Long productId);

    List<ProductImage> selectProductCommonImages(@Param("productId") Long productId);

    List<ProductAddon> selectProductAddons(@Param("productId") Long productId);

    List<ProductAddon> selectAllAddons();

    // 애드온 관리(등록/수정/삭제)
    void insertAddon(ProductAddon addon);

    void updateAddon(ProductAddon addon);

    void softDeleteAddon(@Param("productAddonId") Long productAddonId);

    // 상품 등록/수정
    void insertProduct(ProductUpdateRequest productUpdateRequest);

    void updateProduct(ProductUpdateRequest productUpdateRequest);

    void updateDisplayPrice(@Param("productId") Long productId);

    // variant
    void insertVariant(ProductVariant variant);

    void updateVariant(ProductVariant variant);

    void softDeleteVariant(@Param("productVariantId") Long productVariantId);

    List<Long> selectActiveVariantIds(@Param("productId") Long productId);

    // 이미지
    void insertImage(ProductImage image);

    void deleteImagesByProduct(@Param("productId") Long productId);

    List<String> selectAllImageUrls(@Param("productId") Long productId);

    String selectVariantThumbnailUrl(@Param("productVariantId") Long productVariantId);

    void deleteVariantThumbnail(@Param("productVariantId") Long productVariantId);

    void deleteImageByUrl(@Param("url") String url);

    // 애드온 매핑
    void deleteAddonMapByProduct(@Param("productId") Long productId);

    void insertAddonMap(@Param("productId") Long productId, @Param("addonId") Long addonId);

    // 상품 문의
    long selectProductInquiryCount(ProductInquiryListSearchRequest productInquiryListSearchRequest);

    List<Inquiry> selectProductInquiryList(ProductInquiryListSearchRequest productInquiryListSearchRequest);
}
