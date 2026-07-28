package com.gwana.server.controller;

import com.gwana.server.common.utils.ApiResponse;
import com.gwana.server.dto.cart.Cart;
import com.gwana.server.dto.order.Order;
import com.gwana.server.dto.order.OrderSearchRequest;
import com.gwana.server.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/order")
@Tag(name = "주문", description = "주문 생성 / 조회 (인증 필요)")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "주문 생성", description = "장바구니 목록으로 주문을 생성하고 주문번호 반환")
    @PostMapping("/create")
    public ApiResponse<String> createOrder(@RequestBody List<Cart> cartList) {
        String orderId = orderService.createOrder(cartList);
        return ApiResponse.ok(orderId);
    }

    @Operation(summary = "주문 조회")
    @PostMapping("/search")
    public ApiResponse<Order> searchOrder(@RequestBody OrderSearchRequest orderSearchRequest) {
        Order order = orderService.searchOrder(orderSearchRequest);
        return ApiResponse.ok(order);
    }
}
