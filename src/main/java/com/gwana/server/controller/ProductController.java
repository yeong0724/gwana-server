package com.gwana.server.controller;

import com.gwana.server.common.utils.ApiResponse;
import com.gwana.server.dto.InfiniteResponse;
import com.gwana.server.dto.mypage.Inquiry;
import com.gwana.server.dto.mypage.ProductInquiryListSearchRequest;
import com.gwana.server.dto.product.*;
import com.gwana.server.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    @PostMapping("/list/search")
    public ApiResponse<List<Product>> searchProducts(@RequestBody ProductListRequest productListRequest) {
        return ApiResponse.ok(productService.getProductList(productListRequest));
    }

    @PostMapping("/detail/search")
    public ApiResponse<ProductDetailResponse> searchProduct(@RequestBody ProductRequest productRequest) {
        return ApiResponse.ok(productService.getProductDetail(productRequest));
    }

    /**
     * 상품상세 문의목록 조회
     */
    @PostMapping("/inquiry/list/search")
    public ApiResponse<InfiniteResponse<List<Inquiry>>> productsInquiryListSearch(
            @RequestBody ProductInquiryListSearchRequest productInquiryListSearchRequest
    ) {
        return ApiResponse.ok(productService.productsInquiryListSearch(productInquiryListSearchRequest));
    }
}
