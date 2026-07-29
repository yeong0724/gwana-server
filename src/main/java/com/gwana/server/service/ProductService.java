package com.gwana.server.service;

import com.gwana.server.client.S3UploadClient;
import com.gwana.server.common.enums.ErrorCode;
import com.gwana.server.common.enums.ProductStatus;
import com.gwana.server.common.exception.CustomException;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    private final ProductMapper productMapper;
    private final S3UploadClient s3UploadClient;
    private final JwtTokenProvider jwtTokenProvider;

    // 업로드 제한은 서버에서 고정 (클라이언트가 조정 불가)
    private static final long PRODUCT_IMAGE_MAX_SIZE = 3 * 1024 * 1024;

    // 이미지 실폴더
    private static final String GALLERY_FOLDER = "images/product/thumbnail";
    private static final String DETAIL_FOLDER = "images/product/info";
    private static final String VARIANT_THUMB_FOLDER = "images/product/variant";

    // 등록 모드에서 임시 업로드된 이미지 키 형식 (임의 객체 복사 방지)
    private static final java.util.regex.Pattern PRODUCT_TEMP_KEY_PATTERN =
            java.util.regex.Pattern.compile("^temp/product/(thumbnail|info|variant)/[0-9A-Za-z._-]+$");

    // ================= 조회 =================

    public List<Product> getProductList(ProductListRequest productListRequest) {
        return productMapper.selectProductList(productListRequest);
    }

    /**
     * 관리자용 상품 목록. 전체 상태(임시저장/숨김 포함)를 조회한다. 삭제된 것만 제외.
     */
    public List<Product> getAdminProductList(ProductListRequest productListRequest) {
        return productMapper.selectAdminProductList(productListRequest);
    }

    /**
     * 관리자 상품 상태 변경. product_status enum 값만 허용.
     */
    public void updateProductStatus(ProductStatusUpdateRequest req) {
        if (!ProductStatus.isValid(req.getStatus())) {
            throw new CustomException(
                    ErrorCode.INVALID_PRODUCT_STATUS.getCode(),
                    ErrorCode.INVALID_PRODUCT_STATUS.getMessage());
        }
        productMapper.updateProductStatus(req.getProductId(), req.getStatus());
    }

    public Product getProduct(Long productId) {
        return productMapper.selectProduct(productId);
    }

    public ProductVariant getProductVariant(Long productVariantId) {
        return productMapper.selectProductVariant(productVariantId);
    }

    /**
     * 관리자 상품 등록/수정 화면에서 추가선택옵션(애드온) 후보로 노출할 활성 애드온 전체 목록.
     */
    public List<ProductAddon> getAllAddons() {
        return productMapper.selectAllAddons();
    }

    // ================= 애드온 관리(등록/수정/삭제) =================

    public void createAddon(ProductAddon addon) {
        productMapper.insertAddon(addon);
    }

    public void updateAddon(ProductAddon addon) {
        productMapper.updateAddon(addon);
    }

    public void deleteAddon(Long productAddonId) {
        productMapper.softDeleteAddon(productAddonId);
    }

    // 고객에게 노출 가능한 상태 (임시저장/숨김은 제외)
    private static final java.util.Set<String> CUSTOMER_VISIBLE_STATUS =
            java.util.Set.of("ON_SALE", "SOLD_OUT", "DISCONTINUED");

    /**
     * 고객용 상세 조회. 숨김(HIDDEN)/미존재/삭제면 "존재하지 않는 상품" 예외.
     */
    public ProductDetailResponse getProductDetail(ProductRequest productRequest) {
        Product product = getProduct(productRequest.getProductId());
        if (product == null || !CUSTOMER_VISIBLE_STATUS.contains(product.getStatus())) {
            throw new CustomException(
                    ErrorCode.PRODUCT_NOT_FOUND.getCode(),
                    ErrorCode.PRODUCT_NOT_FOUND.getMessage());
        }
        return buildProductDetail(product);
    }

    /**
     * 관리자용 상세 조회. 상태 무관(임시저장/숨김 포함) 편집을 위해 조회. 삭제/미존재만 예외.
     */
    public ProductDetailResponse getAdminProductDetail(ProductRequest productRequest) {
        Product product = getProduct(productRequest.getProductId());
        if (product == null) {
            throw new CustomException(
                    ErrorCode.PRODUCT_NOT_FOUND.getCode(),
                    ErrorCode.PRODUCT_NOT_FOUND.getMessage());
        }
        return buildProductDetail(product);
    }

    private ProductDetailResponse buildProductDetail(Product product) {
        Long productId = product.getProductId();

        List<ProductVariant> variants = productMapper.selectProductVariants(productId);
        List<ProductImage> commonImages = productMapper.selectProductCommonImages(productId);
        List<ProductAddon> addons = productMapper.selectProductAddons(productId);

        List<String> gallery = new ArrayList<>();
        List<String> detail = new ArrayList<>();
        for (ProductImage image : commonImages) {
            if ("DETAIL".equals(image.getImageType())) {
                detail.add(image.getUrl());
            } else if ("GALLERY".equals(image.getImageType())) {
                gallery.add(image.getUrl());
            }
        }

        return ProductDetailResponse.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .categoryId(product.getCategoryId())
                .categoryName(product.getCategoryName())
                .summary(product.getSummary())
                .detailContent(product.getDetailContent())
                .status(product.getStatus())
                .shippingPrice(product.getShippingPrice())
                .displayPrice(product.getDisplayPrice())
                .galleryImages(gallery)
                .detailImages(detail)
                .variants(variants)
                .addons(addons)
                .avgRating(product.getAvgRating())
                .reviewCount(product.getReviewCount())
                .build();
    }

    // ================= 이미지 업로드 =================

    public String uploadProductImage(MultipartFile multipartFile, String folderPath) {
        String contentType = Validate.validateFile(multipartFile, PRODUCT_IMAGE_MAX_SIZE);
        String safeFolder = Validate.validateFolderPath(folderPath);
        return s3UploadClient.uploadImage(multipartFile, safeFolder, contentType);
    }

    public void deleteProductImage(String imageUrl) {
        productMapper.deleteImageByUrl(imageUrl);
        s3UploadClient.deleteImage(imageUrl);
    }

    // ================= 상품 등록/수정 =================

    /**
     * 상품 등록(신규 전용). 이미지는 temp 에 올라와 있으므로 실폴더로 이동 후 DB 저장한다.
     * DB 실패 시 이미 옮긴 실폴더 객체는 best-effort 로 정리한다. (temp 원본은 S3 lifecycle 이 청소)
     */
    public ProductDetailResponse createProduct(ProductUpdateRequest req) {
        List<String> movedRealKeys = new ArrayList<>();
        try {
            req.setGalleryUrls(moveTempImages(req.getGalleryUrls(), GALLERY_FOLDER, movedRealKeys));
            req.setDetailUrls(moveTempImages(req.getDetailUrls(), DETAIL_FOLDER, movedRealKeys));
            if (req.getVariants() != null) {
                for (VariantUpsertRequest v : req.getVariants()) {
                    v.setThumbnailUrl(moveTempImage(v.getThumbnailUrl(), VARIANT_THUMB_FOLDER, movedRealKeys));
                }
            }

            productMapper.insertProduct(req);              // productId 채워짐
            Long productId = req.getProductId();

            saveVariants(productId, req.getVariants());
            replaceCommonImages(productId, req.getGalleryUrls(), req.getDetailUrls());
            insertVariantThumbnails(productId, req.getVariants());
            replaceAddonMap(productId, req.getAddonIds());

            productMapper.updateDisplayPrice(productId);

            return buildProductDetail(getProduct(productId));
        } catch (RuntimeException e) {
            deleteQuietly(movedRealKeys);
            throw e;
        }
    }

    /**
     * 상품 수정(일괄 배치). 등록과 동일하게 "원하는 최종 상태" 전체를 받아 reconcile 한다.
     * - 스칼라(상품명/카테고리/summary/배송비) 반영
     * - 이미지: temp→실폴더 이동, 최종 목록에 없는 기존 이미지 S3 삭제, 행 통째 교체
     * - 옵션: 목록에 없는 활성 variant soft delete, 나머지 upsert(sortOrder=index 는 프론트가 지정)
     * - 애드온: 맵 통째 교체
     * DB 실패 시 옮긴 실폴더 객체는 best-effort 정리. (temp 원본은 S3 lifecycle 이 청소)
     */
    public void updateProduct(ProductUpdateRequest req) {
        Long productId = req.getProductId();
        List<String> movedRealKeys = new ArrayList<>();
        try {
            // 0) 이미지 temp → 실폴더 이동 (기존 실 URL 은 그대로 유지)
            req.setGalleryUrls(moveTempImages(req.getGalleryUrls(), GALLERY_FOLDER, movedRealKeys));
            req.setDetailUrls(moveTempImages(req.getDetailUrls(), DETAIL_FOLDER, movedRealKeys));
            if (req.getVariants() != null) {
                for (VariantUpsertRequest v : req.getVariants()) {
                    v.setThumbnailUrl(moveTempImage(v.getThumbnailUrl(), VARIANT_THUMB_FOLDER, movedRealKeys));
                }
            }

            // 1) 스칼라
            productMapper.updateProduct(req);

            // 2) 제거될 이미지 S3 정리 — 최종 URL 집합에 없는 기존 이미지 삭제 (행은 아래 blanket 으로 교체)
            Set<String> nextUrls = new HashSet<>();
            if (req.getGalleryUrls() != null) nextUrls.addAll(req.getGalleryUrls());
            if (req.getDetailUrls() != null) nextUrls.addAll(req.getDetailUrls());
            if (req.getVariants() != null) {
                for (VariantUpsertRequest v : req.getVariants()) {
                    if (v.getThumbnailUrl() != null && !v.getThumbnailUrl().isBlank()) {
                        nextUrls.add(v.getThumbnailUrl());
                    }
                }
            }
            for (String oldUrl : productMapper.selectAllImageUrls(productId)) {
                if (!nextUrls.contains(oldUrl)) {
                    deleteQuietly(List.of(oldUrl));
                }
            }

            // 3) 옵션 reconcile — 목록에 없는(=삭제 의도) 활성 variant soft delete
            Set<Long> incomingIds = new HashSet<>();
            if (req.getVariants() != null) {
                for (VariantUpsertRequest v : req.getVariants()) {
                    if (v.getProductVariantId() != null) {
                        incomingIds.add(v.getProductVariantId());
                    }
                }
            }
            for (Long id : productMapper.selectActiveVariantIds(productId)) {
                if (!incomingIds.contains(id)) {
                    productMapper.softDeleteVariant(id);
                }
            }

            // 4) 저장 (등록과 동일 시퀀스). replaceCommonImages 가 product_image 전량 삭제 후 재삽입.
            saveVariants(productId, req.getVariants());
            replaceCommonImages(productId, req.getGalleryUrls(), req.getDetailUrls());
            insertVariantThumbnails(productId, req.getVariants());
            replaceAddonMap(productId, req.getAddonIds());

            productMapper.updateDisplayPrice(productId);
        } catch (RuntimeException e) {
            deleteQuietly(movedRealKeys);
            throw e;
        }
    }

    public void deleteProductVariant(ProductVariantDeleteRequest req) {
        Long variantId = req.getProductVariantId();

        // 옵션은 soft delete 로 남기되, 표시 자산인 썸네일(product_image + S3)은 정리한다.
        String thumbUrl = productMapper.selectVariantThumbnailUrl(variantId);
        productMapper.softDeleteVariant(variantId);
        productMapper.deleteVariantThumbnail(variantId);
        if (thumbUrl != null) {
            deleteQuietly(List.of(thumbUrl));
        }

        if (req.getProductId() != null) {
            productMapper.updateDisplayPrice(req.getProductId());
        }
    }

    /** temp 이미지 목록을 실폴더로 이동하고 실 키 목록을 반환. 옮긴 키는 rollback 정리용으로 모은다. */
    private List<String> moveTempImages(List<String> urls, String destFolder, List<String> movedRealKeys) {
        List<String> result = new ArrayList<>();
        if (urls == null) {
            return result;
        }
        for (String url : urls) {
            result.add(moveTempImage(url, destFolder, movedRealKeys));
        }
        return result;
    }

    /** temp 키면 실폴더로 이동 후 실 키 반환. temp 가 아니면(이미 실 키) 그대로 둔다. */
    private String moveTempImage(String url, String destFolder, List<String> movedRealKeys) {
        if (url == null || !url.startsWith("temp/")) {
            return url;
        }
        if (!PRODUCT_TEMP_KEY_PATTERN.matcher(url).matches()) {
            throw new CustomException(
                    ErrorCode.INVALID_IMAGE_REFERENCE.getCode(),
                    ErrorCode.INVALID_IMAGE_REFERENCE.getMessage());
        }
        String realKey = s3UploadClient.moveImage(url, destFolder);
        movedRealKeys.add(realKey);
        return realKey;
    }

    /** S3 객체 best-effort 삭제 (실패해도 예외 전파하지 않음) */
    private void deleteQuietly(List<String> keys) {
        for (String key : keys) {
            try {
                s3UploadClient.deleteImage(key);
            } catch (RuntimeException ex) {
                // best-effort: 정리 실패는 로깅만
            }
        }
    }

    /**
     * variant upsert. 신규는 insert, 기존은 update. 각 variant 썸네일은 재설정(교체).
     * 공통 이미지(replaceCommonImages) 에서 product_image 를 전량 삭제하므로,
     * variant 썸네일은 여기서 다시 insert 한다.
     */
    private void saveVariants(Long productId, List<VariantUpsertRequest> variants) {
        if (variants == null) {
            return;
        }
        for (VariantUpsertRequest v : variants) {
            ProductVariant variant = ProductVariant.builder()
                    .productVariantId(v.getProductVariantId())
                    .productId(productId)
                    .optionLabel(v.getOptionLabel())
                    .price(v.getPrice())
                    .status(v.getStatus())
                    .sortOrder(v.getSortOrder())
                    .build();

            if (v.getProductVariantId() == null) {
                productMapper.insertVariant(variant);       // productVariantId 채워짐
            } else {
                productMapper.updateVariant(variant);
            }
            // variant 썸네일은 replaceCommonImages 에서 재삽입되도록 req 에 보관된 url 을 그대로 사용한다.
            v.setProductVariantId(variant.getProductVariantId());
        }
    }

    /**
     * 상품 공통 이미지(갤러리/디테일) + variant 썸네일을 전량 교체(replace)한다.
     */
    private void replaceCommonImages(Long productId, List<String> galleryUrls, List<String> detailUrls) {
        productMapper.deleteImagesByProduct(productId);

        if (galleryUrls != null) {
            for (int i = 0; i < galleryUrls.size(); i++) {
                productMapper.insertImage(ProductImage.builder()
                        .productId(productId).imageType("GALLERY")
                        .url(galleryUrls.get(i)).sortOrder(i).build());
            }
        }
        if (detailUrls != null) {
            for (int i = 0; i < detailUrls.size(); i++) {
                productMapper.insertImage(ProductImage.builder()
                        .productId(productId).imageType("DETAIL")
                        .url(detailUrls.get(i)).sortOrder(i).build());
            }
        }
    }

    /**
     * variant 썸네일 이미지 삽입. saveVariants 로 variant id 확정 후 호출.
     */
    private void insertVariantThumbnails(Long productId, List<VariantUpsertRequest> variants) {
        if (variants == null) {
            return;
        }
        for (VariantUpsertRequest v : variants) {
            if (v.getThumbnailUrl() != null && !v.getThumbnailUrl().isBlank()) {
                productMapper.insertImage(ProductImage.builder()
                        .productId(productId)
                        .productVariantId(v.getProductVariantId())
                        .imageType("THUMBNAIL")
                        .url(v.getThumbnailUrl())
                        .sortOrder(0)
                        .build());
            }
        }
    }

    private void replaceAddonMap(Long productId, List<Long> addonIds) {
        productMapper.deleteAddonMapByProduct(productId);
        if (addonIds != null) {
            for (Long addonId : addonIds) {
                productMapper.insertAddonMap(productId, addonId);
            }
        }
    }

    // ================= 상품 문의 =================

    public InfiniteResponse<List<Inquiry>> productsInquiryListSearch(ProductInquiryListSearchRequest req) {
        injectAuthInfo(req);

        int page = req.getPage();
        int size = req.getSize();
        req.setOffset(page * size);

        long totalCount = productMapper.selectProductInquiryCount(req);
        boolean hasNext = (long) (page + 1) * size < totalCount;
        List<Inquiry> list = productMapper.selectProductInquiryList(req);

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
