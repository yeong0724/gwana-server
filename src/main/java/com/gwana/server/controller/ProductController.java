package com.gwana.server.controller;

import com.gwana.server.common.utils.ApiResponse;
import com.gwana.server.dto.InfiniteResponse;
import com.gwana.server.dto.mypage.Inquiry;
import com.gwana.server.dto.mypage.ProductInquiryListSearchRequest;
import com.gwana.server.dto.product.*;
import com.gwana.server.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/product")
@Tag(name = "상품", description = "상품 목록 / 상세 / 상품 문의 조회")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "상품 목록 조회")
    @PostMapping("/list/search")
    public ApiResponse<List<Product>> searchProducts(@RequestBody ProductListRequest productListRequest) {
        return ApiResponse.ok(productService.getProductList(productListRequest));
    }

    @Operation(summary = "상품 상세 조회")
    @PostMapping("/detail/search")
    public ApiResponse<ProductDetailResponse> searchProduct(@RequestBody ProductRequest productRequest) {
        return ApiResponse.ok(productService.getProductDetail(productRequest));
    }

    /**
     * 상품상세 문의목록 조회
     */
    @Operation(summary = "상품 문의 목록 조회")
    @PostMapping("/inquiry/list/search")
    public ApiResponse<InfiniteResponse<List<Inquiry>>> productsInquiryListSearch(
            @RequestBody ProductInquiryListSearchRequest productInquiryListSearchRequest
    ) {
        return ApiResponse.ok(productService.productsInquiryListSearch(productInquiryListSearchRequest));
    }
}
