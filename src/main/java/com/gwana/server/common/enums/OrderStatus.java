package com.gwana.server.common.enums;

import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    PENDING_PAYMENT("결제 대기"),
    PAID("결제 완료"),
    PREPARING("상품 준비중"),
    SHIPPED("배송중"),
    DELIVERED("배송 완료"),
    CANCELLED("주문 취소"),
    REFUNDED("환불 완료");

    private final String description;

    // 허용된 상태 전이 정의
    private static final java.util.Map<OrderStatus, Set<OrderStatus>> TRANSITIONS = java.util.Map.of(
            PENDING_PAYMENT, Set.of(PAID, CANCELLED),
            PAID,            Set.of(PREPARING, CANCELLED, REFUNDED),
            PREPARING,       Set.of(SHIPPED, CANCELLED, REFUNDED),
            SHIPPED,         Set.of(DELIVERED, REFUNDED),
            DELIVERED,       Set.of(REFUNDED),
            CANCELLED,       Set.of(),
            REFUNDED,        Set.of()
    );

    public boolean canTransitionTo(OrderStatus next) {
        return TRANSITIONS.getOrDefault(this, Set.of()).contains(next);
    }

    public boolean isTerminal() {
        return this == DELIVERED || this == CANCELLED || this == REFUNDED;
    }

    public boolean isPaid() {
        return this != PENDING_PAYMENT && this != CANCELLED;
    }
}
