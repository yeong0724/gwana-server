package com.gwana.server.controller;

import com.gwana.server.common.utils.ApiResponse;
import com.gwana.server.dto.product.ProductListRequest;
import com.gwana.server.dto.product.ProductRequest;
import com.gwana.server.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import com.gwana.server.dto.product.ProductListResponse;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/product")
public class ProductController {

	private final ProductService productService;

	@PostMapping("/list")
	public ApiResponse<List<ProductListResponse>> searchProducts(@RequestBody ProductListRequest productListRequest) {
		;
		return ApiResponse.ok(productService.getProductList(productListRequest));
	}

	@PostMapping("/detail")
	public ApiResponse<ProductListResponse> searchProduct(@RequestBody ProductRequest productRequest) {
		;
		return ApiResponse.ok(productService.getProduct(productRequest));
	}
}
