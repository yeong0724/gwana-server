package com.gwana.server.controller;

import com.gwana.server.common.utils.ApiResponse;
import com.gwana.server.dto.cart.*;
import com.gwana.server.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;

    @GetMapping("/list/search")
    public ApiResponse<List<Cart>> searchCartList() {
        return ApiResponse.ok(cartService.searchCartList());
    }

    @PostMapping("/upsert")
    public ApiResponse<Void> upsertCart(@RequestBody UpsertCartRequest upsertCartRequest) {
        cartService.upsertCart(upsertCartRequest);
        return ApiResponse.ok(null);
    }

    @PostMapping("/list/upsert")
    public ApiResponse<Void> updateCart(@RequestBody List<UpsertCartRequest> upsertCartRequests) {
        cartService.upsertCartList(upsertCartRequests);
        return ApiResponse.ok(null);
    }

    @PostMapping("/cart-item/delete")
    public ApiResponse<Void> deleteCartItem(@RequestBody CartItemDeleteRequest cartItemDeleteRequest) {
        cartService.deleteCartItem(cartItemDeleteRequest);
        return ApiResponse.ok(null);
    }

    @PostMapping("/delete")
    public ApiResponse<Void> deleteCart(@RequestBody CartDeleteRequest cartDeleteRequest) {
        cartService.deleteCart(cartDeleteRequest);
        return ApiResponse.ok(null);
    }

    @PostMapping("/list/delete")
    public ApiResponse<Void> deleteCartList(@RequestBody List<Long> cartIdList) {
        cartService.deleteCartList(cartIdList);
        return ApiResponse.ok(null);
    }

    @PostMapping("/quantity/update")
    public ApiResponse<Void> updateCartItemQuantity(@RequestBody CartItemUpdateRequest cartItemUpdateRequest) {
        cartService.updateCartItemQuantity(cartItemUpdateRequest);
        return ApiResponse.ok(null);
    }
}