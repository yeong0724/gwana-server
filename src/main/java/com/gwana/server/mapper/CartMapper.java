package com.gwana.server.mapper;

import com.gwana.server.dto.cart.CartUpdateRequest;
import com.gwana.server.dto.cart.CartResponse;
import com.gwana.server.dto.cart.PaymentSessionRequest;
import com.gwana.server.dto.cart.PaymentSessionResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CartMapper {
    List<CartResponse> selectCartList(String userId);

    void upsertCart(CartUpdateRequest cartUpdateRequest);

    void deleteCart(String cartId);

    void updateCartQuantity(CartUpdateRequest cartUpdateRequest);

    void deletePaymentSession(String userId);

    void createPaymentSession(PaymentSessionRequest paymentSessionRequest);

    List<PaymentSessionResponse> getPaymentSession(String sessionId);
}
