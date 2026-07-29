package com.gwana.server.controller;

import com.gwana.server.common.utils.ApiResponse;
import com.gwana.server.dto.product.Product;
import com.gwana.server.dto.product.ProductAddon;
import com.gwana.server.dto.product.ProductAddonDeleteRequest;
import com.gwana.server.dto.product.ProductDetailResponse;
import com.gwana.server.dto.product.ProductImageDeleteRequest;
import com.gwana.server.dto.product.ProductListRequest;
import com.gwana.server.dto.product.ProductRequest;
import com.gwana.server.dto.product.ProductStatusUpdateRequest;
import com.gwana.server.dto.product.ProductVariantDeleteRequest;
import com.gwana.server.dto.product.ProductUpdateRequest;
import com.gwana.server.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
@Tag(name = "관리자", description = "상품 등록/수정/삭제, 이미지 업로드 (ADMIN 권한 필요)")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {
    private final ProductService productService;

    @Operation(summary = "상품 이미지 업로드")
    @PostMapping("/product/image/upload")
    public ApiResponse<String> uploadProductImage(
            @RequestParam("image") MultipartFile multipartFile,
            @RequestParam(value = "folderPath", defaultValue = "") String folderPath
    ) {
        return ApiResponse.ok(productService.uploadProductImage(multipartFile, folderPath));
    }

    @Operation(summary = "애드온(추가선택옵션) 목록 조회", description = "상품 등록/수정 시 선택 후보로 노출할 활성 애드온 전체")
    @GetMapping("/product/addons")
    public ApiResponse<List<ProductAddon>> getAddons() {
        return ApiResponse.ok(productService.getAllAddons());
    }

    @Operation(summary = "애드온 등록")
    @PostMapping("/product/addon/create")
    public ApiResponse<Void> createAddon(@RequestBody ProductAddon productAddon) {
        productService.createAddon(productAddon);
        return ApiResponse.ok(null);
    }

    @Operation(summary = "애드온 수정")
    @PostMapping("/product/addon/update")
    public ApiResponse<Void> updateAddon(@RequestBody ProductAddon productAddon) {
        productService.updateAddon(productAddon);
        return ApiResponse.ok(null);
    }

    @Operation(summary = "애드온 삭제", description = "소프트 삭제(deleted_at 세팅 → is_deleted)")
    @PostMapping("/product/addon/delete")
    public ApiResponse<Void> deleteAddon(@RequestBody ProductAddonDeleteRequest request) {
        productService.deleteAddon(request.getProductAddonId());
        return ApiResponse.ok(null);
    }

    @Operation(summary = "관리자 상품 목록 조회", description = "전체 상태(임시저장/숨김 포함) 조회. 삭제된 것만 제외")
    @PostMapping("/product/list")
    public ApiResponse<List<Product>> getAdminProductList(@RequestBody ProductListRequest productListRequest) {
        return ApiResponse.ok(productService.getAdminProductList(productListRequest));
    }

    @Operation(summary = "상품 상태 변경", description = "ON_SALE/SOLD_OUT/HIDDEN/DISCONTINUED")
    @PostMapping("/product/status/update")
    public ApiResponse<Void> updateProductStatus(@RequestBody ProductStatusUpdateRequest request) {
        productService.updateProductStatus(request);
        return ApiResponse.ok(null);
    }

    @Operation(summary = "관리자 상품 상세 조회", description = "상태 무관(임시저장/숨김 포함) 편집용 상세")
    @PostMapping("/product/detail")
    public ApiResponse<ProductDetailResponse> getAdminProductDetail(@RequestBody ProductRequest productRequest) {
        return ApiResponse.ok(productService.getAdminProductDetail(productRequest));
    }

    @Operation(summary = "상품 등록", description = "이미지는 temp→실폴더 이동 후 전량 저장. 생성된 상품 상세 반환")
    @PostMapping("/product/create")
    public ApiResponse<ProductDetailResponse> createProduct(@RequestBody ProductUpdateRequest productUpdateRequest) {
        return ApiResponse.ok(productService.createProduct(productUpdateRequest));
    }

    @Operation(summary = "상품 수정(일괄 배치)", description = "원하는 최종 상태(스칼라+옵션+이미지+애드온) 전체를 받아 reconcile. 이미지는 temp→실폴더 이동")
    @PostMapping("/product/update")
    public ApiResponse<Void> updateProduct(@RequestBody ProductUpdateRequest productUpdateRequest) {
        productService.updateProduct(productUpdateRequest);
        return ApiResponse.ok(null);
    }

    @Operation(summary = "상품 variant(판매단위) 삭제")
    @PostMapping("/product/variant/delete")
    public ApiResponse<Void> deleteProductVariant(@RequestBody ProductVariantDeleteRequest productVariantDeleteRequest) {
        productService.deleteProductVariant(productVariantDeleteRequest);
        return ApiResponse.ok(null);
    }

    @Operation(summary = "상품 이미지 삭제")
    @PostMapping("/product/image/delete")
    public ApiResponse<Void> deleteProductImage(@RequestBody ProductImageDeleteRequest productImageDeleteRequest) {
        String imageUrl = productImageDeleteRequest.getImageUrl();
        productService.deleteProductImage(imageUrl);
        return ApiResponse.ok(null);
    }
}
