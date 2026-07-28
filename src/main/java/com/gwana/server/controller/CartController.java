package com.gwana.server.controller;

import com.gwana.server.common.utils.ApiResponse;
import com.gwana.server.dto.cart.*;
import com.gwana.server.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/cart")
@Tag(name = "장바구니", description = "장바구니 조회 / 담기 / 수정 / 삭제 (인증 필요)")
@SecurityRequirement(name = "bearerAuth")
public class CartController {
    private final CartService cartService;

    @Operation(summary = "장바구니 목록 조회")
    @GetMapping("/list/search")
    public ApiResponse<List<Cart>> searchCartList() {
        return ApiResponse.ok(cartService.searchCartList());
    }

    @Operation(summary = "장바구니 담기/수정 (단건)")
    @PostMapping("/upsert")
    public ApiResponse<Void> upsertCart(@RequestBody UpsertCartRequest upsertCartRequest) {
        cartService.upsertCart(upsertCartRequest);
        return ApiResponse.ok(null);
    }

    @Operation(summary = "장바구니 담기/수정 (다건)")
    @PostMapping("/list/upsert")
    public ApiResponse<Void> updateCart(@RequestBody List<UpsertCartRequest> upsertCartRequests) {
        cartService.upsertCartList(upsertCartRequests);
        return ApiResponse.ok(null);
    }

    @Operation(summary = "장바구니 항목 삭제")
    @PostMapping("/cart-item/delete")
    public ApiResponse<Void> deleteCartItem(@RequestBody CartItemDeleteRequest cartItemDeleteRequest) {
        cartService.deleteCartItem(cartItemDeleteRequest);
        return ApiResponse.ok(null);
    }

    @Operation(summary = "장바구니(상품 단위) 삭제")
    @PostMapping("/delete")
    public ApiResponse<Void> deleteCart(@RequestBody CartDeleteRequest cartDeleteRequest) {
        cartService.deleteCart(cartDeleteRequest);
        return ApiResponse.ok(null);
    }

    @Operation(summary = "장바구니 다건 삭제")
    @PostMapping("/list/delete")
    public ApiResponse<Void> deleteCartList(@RequestBody List<Long> cartIdList) {
        cartService.deleteCartList(cartIdList);
        return ApiResponse.ok(null);
    }

    @Operation(summary = "장바구니 항목 수량 변경")
    @PostMapping("/quantity/update")
    public ApiResponse<Void> updateCartItemQuantity(@RequestBody CartItemUpdateRequest cartItemUpdateRequest) {
        cartService.updateCartItemQuantity(cartItemUpdateRequest);
        return ApiResponse.ok(null);
    }
}