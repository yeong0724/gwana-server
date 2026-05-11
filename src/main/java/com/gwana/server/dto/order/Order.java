package com.gwana.server.dto.order;

import com.gwana.server.common.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    // Order ID
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

    // 주문자 이름
    private String senderName;

    // 주문자 연락처
    private String senderPhone;

    // 수령자 이름
    private String recipientName;

    // 수령자 연락차
    private String recipientPhone;

    // 우편번호
    private String zonecode;

    // 도로명 주소
    private String roadAddress;

    // 상세 주소
    private String detailAddress;

    // 배송 요청사항
    private String deliveryRequest;

    // 요청사항 내용
    private String deliveryRequestDetail;

    // 주문 상품 목록
    private List<OrderOptionGroup> orderOptionGroups;
}
