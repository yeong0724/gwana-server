package com.gwana.server.dto.order;

import com.gwana.server.common.enums.OrderStatus;
import com.gwana.server.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CreateOrder extends BaseDto {
    private String orderId;

    // 상품 합계
    private int productAmount;

    // 배송비
    private int shippingFee;

    // 할인 금액
    private int discountAmount;

    // 총 결제 금액
    private int totalAmount;

    // 주문 현황
    private OrderStatus orderStatus;
}
