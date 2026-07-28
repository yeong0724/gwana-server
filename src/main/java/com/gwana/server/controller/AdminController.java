package com.gwana.server.controller;

import com.gwana.server.common.utils.ApiResponse;
import com.gwana.server.dto.product.ProductImageDeleteRequest;
import com.gwana.server.dto.product.ProductOptionDeleteRequest;
import com.gwana.server.dto.product.ProductUpdateRequest;
import com.gwana.server.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @Operation(summary = "상품 등록")
    @PostMapping("/product/create")
    public ApiResponse<Void> createProduct(@RequestBody ProductUpdateRequest productUpdateRequest) {
        productService.createProduct(productUpdateRequest);
        return ApiResponse.ok(null);
    }

    @Operation(summary = "상품 수정")
    @PostMapping("/product/update")
    public ApiResponse<Void> updateProduct(@RequestBody ProductUpdateRequest productUpdateRequest) {
        productService.updateProduct(productUpdateRequest);
        return ApiResponse.ok(null);
    }

    @Operation(summary = "상품 옵션 삭제")
    @PostMapping("/product/option/delete")
    public ApiResponse<Void> updateProduct(@RequestBody ProductOptionDeleteRequest productOptionDeleteRequest) {
        productService.deleteProductOption(productOptionDeleteRequest);
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
