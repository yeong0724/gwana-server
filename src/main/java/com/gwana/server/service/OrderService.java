package com.gwana.server.service;

import com.gwana.server.common.enums.OrderStatus;
import com.gwana.server.common.security.JwtTokenProvider;
import com.gwana.server.dto.cart.Cart;
import com.gwana.server.dto.cart.CartItem;
import com.gwana.server.dto.order.*;
import com.gwana.server.dto.product.Product;
import com.gwana.server.dto.product.ProductVariant;
import com.gwana.server.dto.user.AuthUser;
import com.gwana.server.mapper.OrderMapper;
import io.hypersistence.tsid.TSID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Collator;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {
    private final JwtTokenProvider jwtTokenProvider;
    private final ProductService productService;

    private final OrderMapper orderMapper;

    public String createOrder(List<Cart> cartList) {
        String orderId = TSID.Factory.getTsid().toString(); // 주문번호

        int productAmount = 0; // 상품금액 합계
        int shippingFee = 0; // 배송비
        int discountAmount = 0; // 할인금액
        List<CreateOrderItem> createOrderItems = new ArrayList<>();

        for (Cart cart : cartList) {
            Long productId = cart.getProductId();

            Product product = productService.getProduct(productId);

            String productName = product.getName();
            String categoryName = product.getCategoryName();
            String productThumbnailUrl = product.getThumbnailUrl();
            shippingFee += product.getShippingPrice();   // 상품별 배송비(0=무료) 합산

            List<CartItem> cartItems = cart.getCartItems();

            for (CartItem cartItem : cartItems) {
                int quantity = cartItem.getQuantity();
                Long productVariantId = cartItem.getProductVariantId();
                ProductVariant variant = productService.getProductVariant(productVariantId);
                int price = variant.getPrice();   // 가격은 서버(variant)에서만 결정

                productAmount += quantity * price;

                CreateOrderItem createOrderItem = CreateOrderItem.builder()
                        .orderId(orderId)
                        .productId(productId)
                        .productName(productName)
                        .productThumbnailUrl(productThumbnailUrl)
                        .categoryName(categoryName)
                        .productVariantId(productVariantId)
                        .optionName(variant.getOptionLabel())
                        .optionPrice(price)
                        .quantity(quantity)
                        .isRequired(true)
                        .build();

                createOrderItems.add(createOrderItem);
            }
        }

        int totalAmount = productAmount + shippingFee - discountAmount;

        CreateOrder createOrder = CreateOrder.builder()
                .orderId(orderId)
                .productAmount(productAmount)
                .shippingFee(shippingFee)
                .discountAmount(discountAmount)
                .totalAmount(totalAmount)
                .orderStatus(OrderStatus.PENDING_PAYMENT)
                .build();

        int result = orderMapper.insertOrder(createOrder);

        if (result > 0 && !createOrderItems.isEmpty()) {
            orderMapper.insertOrderItems(createOrderItems);
        }

        return orderId;
    }

    public Order searchOrder(OrderSearchRequest orderSearchRequest) {
        Order order = orderMapper.selectOrder(orderSearchRequest);

        List<OrderItem> orderItems = orderMapper.selectOrderItems(orderSearchRequest);

        Collator korean = Collator.getInstance(Locale.KOREAN);

        List<OrderOptionGroup> orderOptionGroups =  orderItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getProductId, LinkedHashMap::new, Collectors.toList()))
                .entrySet()
                .stream()
                .map(entry -> {
                    List<OrderItem> rows = entry.getValue();
                    OrderItem fistRow = rows.get(0);

                    List<OrderOption> orderOptions = rows.stream()
                            .map(option -> OrderOption.builder()
                                    .productVariantId(option.getProductVariantId())
                                    .optionName(option.getOptionName())
                                    .optionPrice(option.getOptionPrice())
                                    .quantity(option.getQuantity())
                                    .isRequired(option.isRequired())
                                    .build())
                            .sorted(Comparator.comparing(OrderOption::getIsRequired).reversed().thenComparing(OrderOption::getOptionName, korean))
                            .toList();

                    return OrderOptionGroup.builder()
                            .productId(entry.getKey())
                            .productName(fistRow.getProductName())
                            .productThumbnailUrl(fistRow.getProductThumbnailUrl())
                            .categoryName(fistRow.getCategoryName())
                            .orderOptions(orderOptions)
                            .build();
                })
                .toList();

        order.setOrderOptionGroups(orderOptionGroups);

        return order;
    }
}
