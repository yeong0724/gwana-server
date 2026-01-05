package com.gwana.server.service;

import com.gwana.server.common.security.JwtTokenProvider;
import com.gwana.server.dto.cart.CartUpdateRequest;
import com.gwana.server.dto.cart.CartResponse;
import com.gwana.server.dto.cart.PaymentSessionRequest;
import com.gwana.server.dto.cart.PaymentSessionResponse;
import com.gwana.server.dto.user.AuthUser;
import com.gwana.server.mapper.CartMapper;
import io.hypersistence.tsid.TSID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CartService {
    private final CartMapper cartMapper;

    private final JwtTokenProvider jwtTokenProvider;

    public void upsertCart(CartUpdateRequest cartUpdateRequest) {
        AuthUser authUser = jwtTokenProvider.getUserInfo();
        String userId = authUser.getUserId();
        cartUpdateRequest.setUserId(userId);

        cartMapper.upsertCart(cartUpdateRequest);
    }

    public List<CartResponse> searchCartList() {
        AuthUser authUser = jwtTokenProvider.getUserInfo();
        String userId = authUser.getUserId();
        return cartMapper.selectCartList(userId);
    }

    public void updateCartList(List<CartUpdateRequest> cartUpdateRequestList) {
        for (CartUpdateRequest cartUpdateRequest : cartUpdateRequestList) {
            this.upsertCart(cartUpdateRequest);
        }
    }

    public void deleteCart(List<String> cartIdList) {
        for (String cartId : cartIdList) {
            cartMapper.deleteCart(cartId);
        }
    }

    public void updateCartQuantity(CartUpdateRequest cartUpdateRequest) {
        cartMapper.updateCartQuantity(cartUpdateRequest);
    }

    public String createPaymentSession(List<PaymentSessionRequest> paymentSessionRequestList) {
        AuthUser authUser = jwtTokenProvider.getUserInfo();
        String userId = authUser.getUserId();

        cartMapper.deletePaymentSession(userId);

        String sessionId = TSID.Factory.getTsid().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(30);

        for (PaymentSessionRequest paymentSessionRequest : paymentSessionRequestList) {
            paymentSessionRequest.setUserId(userId);
            paymentSessionRequest.setSessionId(sessionId);
            paymentSessionRequest.setExpiresAt(expiresAt);

            cartMapper.createPaymentSession(paymentSessionRequest);
        }

        return sessionId;
    }

    public List<PaymentSessionResponse> getPaymentSession(String sessionId) {
        return cartMapper.getPaymentSession(sessionId);
    }
}
