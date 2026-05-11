package com.gwana.server.controller;

import com.gwana.server.common.utils.ApiResponse;
import com.gwana.server.dto.cart.Cart;
import com.gwana.server.dto.order.Order;
import com.gwana.server.dto.order.OrderSearchRequest;
import com.gwana.server.service.OrderService;
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
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create")
    public ApiResponse<String> createOrder(@RequestBody List<Cart> cartList) {
        String orderId = orderService.createOrder(cartList);
        return ApiResponse.ok(orderId);
    }

    @PostMapping("/search")
    public ApiResponse<Order> searchOrder(@RequestBody OrderSearchRequest orderSearchRequest) {
        Order order = orderService.searchOrder(orderSearchRequest);
        return ApiResponse.ok(order);
    }
}
