package com.gwana.server.controller;

import com.gwana.server.common.utils.ApiResponse;
import com.gwana.server.dto.cart.CartUpdateRequest;
import com.gwana.server.dto.cart.CartResponse;
import com.gwana.server.dto.cart.PaymentSessionRequest;
import com.gwana.server.dto.cart.PaymentSessionResponse;
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

    @PostMapping("/add")
    public ApiResponse<Void> addCart(@RequestBody CartUpdateRequest cartUpdateRequest) {
        cartService.upsertCart(cartUpdateRequest);
        return ApiResponse.ok(null);
    }

    @GetMapping("/list")
    public ApiResponse<List<CartResponse>> searchCartList() {
        return ApiResponse.ok(cartService.searchCartList());
    }

    @PostMapping("/update")
    public ApiResponse<Void> updateCart(@RequestBody List<CartUpdateRequest> cartUpdateRequestList) {
        cartService.updateCartList(cartUpdateRequestList);
        return ApiResponse.ok(null);
    }

    @PostMapping("/delete")
    public ApiResponse<Void> deleteCart(@RequestBody List<String> cartIdList) {
        cartService.deleteCart(cartIdList);
        return ApiResponse.ok(null);
    }

    @PostMapping("/update/quantity")
    public ApiResponse<Void> updateCart(@RequestBody CartUpdateRequest cartUpdateRequest) {
        cartService.updateCartQuantity(cartUpdateRequest);
        return ApiResponse.ok(null);
    }

    @PostMapping("/create/payment/session")
    public ApiResponse<String> createPaymentSession(@RequestBody List<PaymentSessionRequest> paymentSessionRequestList) {
        String sessionId = cartService.createPaymentSession(paymentSessionRequestList);

        return ApiResponse.ok(sessionId);
    }

    @PostMapping("/search/payment/session")
    public ApiResponse<List<PaymentSessionResponse>> createPaymentSession(@RequestBody PaymentSessionRequest paymentSessionRequest) {
        String sessionId = paymentSessionRequest.getSessionId();
        return ApiResponse.ok(cartService.getPaymentSession(sessionId));
    }
}