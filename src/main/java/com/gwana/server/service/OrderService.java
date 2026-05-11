package com.gwana.server.service;

import com.gwana.server.common.enums.OrderStatus;
import com.gwana.server.common.security.JwtTokenProvider;
import com.gwana.server.dto.cart.Cart;
import com.gwana.server.dto.cart.CartItem;
import com.gwana.server.dto.order.*;
import com.gwana.server.dto.product.Product;
import com.gwana.server.dto.product.ProductOption;
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

            String productName = product.getProductName();
            String categoryName = product.getCategoryName();
            String productThumbnailUrl = product.getImages()[0];
            shippingFee += product.getShippingPrice();

            List<CartItem> cartItems = cart.getCartItems();

            for (CartItem cartItem : cartItems) {
                int quantity = cartItem.getQuantity();
                Long productOptionId = cartItem.getProductOptionId();
                ProductOption productOption = productService.getProductOption(productOptionId);
                int optionPrice = productOption.getOptionPrice();

                productAmount += quantity * optionPrice;

                CreateOrderItem createOrderItem = CreateOrderItem.builder()
                        .orderId(orderId)
                        .productId(productId)
                        .productName(productName)
                        .productThumbnailUrl(productThumbnailUrl)
                        .categoryName(categoryName)
                        .productOptionId(productOptionId)
                        .optionName(productOption.getOptionName())
                        .optionPrice(optionPrice)
                        .quantity(quantity)
                        .isRequired(productOption.isRequired())
                        .build();

                createOrderItems.add(createOrderItem);
            }
        }

        if (productAmount > 50000) {
            shippingFee = 0;
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
                                    .productOptionId(option.getProductOptionId())
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
