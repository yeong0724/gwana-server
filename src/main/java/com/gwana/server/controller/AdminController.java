package com.gwana.server.controller;

import com.gwana.server.common.utils.ApiResponse;
import com.gwana.server.dto.product.ProductImageDeleteRequest;
import com.gwana.server.dto.product.ProductOptionDeleteRequest;
import com.gwana.server.dto.product.ProductUpdateRequest;
import com.gwana.server.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminController {
    private final ProductService productService;

    @PostMapping("/product/image/upload")
    public ApiResponse<String> uploadProductImage(
            @RequestParam("image") MultipartFile multipartFile,
            @RequestParam(value = "folderPath", defaultValue = "") String folderPath,
            @RequestParam(value = "maxFileSize", required = false, defaultValue = "3") long maxFileSize
    ) {
        return ApiResponse.ok(productService.uploadProductImage(multipartFile, folderPath, maxFileSize));
    }

    @PostMapping("/product/create")
    public ApiResponse<Void> createProduct(@RequestBody ProductUpdateRequest productUpdateRequest) {
        productService.createProduct(productUpdateRequest);
        return ApiResponse.ok(null);
    }

    @PostMapping("/product/update")
    public ApiResponse<Void> updateProduct(@RequestBody ProductUpdateRequest productUpdateRequest) {
        productService.updateProduct(productUpdateRequest);
        return ApiResponse.ok(null);
    }

    @PostMapping("/product/option/delete")
    public ApiResponse<Void> updateProduct(@RequestBody ProductOptionDeleteRequest productOptionDeleteRequest) {
        productService.deleteProductOption(productOptionDeleteRequest);
        return ApiResponse.ok(null);
    }

    @PostMapping("/product/image/delete")
    public ApiResponse<Void> deleteProductImage(@RequestBody ProductImageDeleteRequest productImageDeleteRequest) {
        String imageUrl = productImageDeleteRequest.getImageUrl();
        productService.deleteProductImage(imageUrl);
        return ApiResponse.ok(null);
    }
}
