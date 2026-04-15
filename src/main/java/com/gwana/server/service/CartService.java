package com.gwana.server.service;

import com.gwana.server.common.security.JwtTokenProvider;
import com.gwana.server.dto.cart.*;
import com.gwana.server.dto.user.AuthUser;
import com.gwana.server.mapper.CartMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CartService {
    private final CartMapper cartMapper;

    private final JwtTokenProvider jwtTokenProvider;

    public void upsertCart(UpsertCartRequest upsertCartRequest) {
        Long productId = upsertCartRequest.getProductId();
        AuthUser authUser = jwtTokenProvider.getUserInfo();
        String userId = authUser.getUserId();
        upsertCartRequest.setUserId(userId);

        Long cartId = cartMapper.selectCartByUserAndProduct(productId, userId);
        if (cartId == null) {
            cartMapper.insertCart(upsertCartRequest);
            cartId = upsertCartRequest.getCartId();
        }

        for (UpsertCartItemRequest upsertCartItemRequest : upsertCartRequest.getCartItems()) {
            upsertCartItemRequest.setCartId(cartId);
            cartMapper.upsertCartItem(upsertCartItemRequest);
        }
    }

    public List<Cart> searchCartList() {
        AuthUser authUser = jwtTokenProvider.getUserInfo();
        String userId = authUser.getUserId();

        List<Cart> cartList = cartMapper.selectCartList(userId);

        for (Cart cart : cartList) {
            List<CartItem> cartItemList = cartMapper.selectCartItemList(cart.getCartId());
            cart.setCartItems(cartItemList);
        }

        return cartList;
    }

    public void deleteCartItem(CartItemDeleteRequest cartItemDeleteRequest) {
        cartMapper.deleteCartItem(cartItemDeleteRequest);
    }

    public void upsertCartList(List<UpsertCartRequest> upsertCartRequests) {
        for (UpsertCartRequest upsertCartRequest : upsertCartRequests) {
            this.upsertCart(upsertCartRequest);
        }
    }

    public void deleteCart(CartDeleteRequest cartDeleteRequest) {
        cartMapper.deleteCart(cartDeleteRequest);
        cartMapper.deleteCartItemByCartId(cartDeleteRequest);
    }

    public void deleteCartList(List<Long> cartIdList) {
        for (Long cartId : cartIdList) {
            CartDeleteRequest cartDeleteRequest = new CartDeleteRequest();
            cartDeleteRequest.setCartId(cartId);
            this.deleteCart(cartDeleteRequest);
        }
    }

    public void updateCartItemQuantity(CartItemUpdateRequest cartItemUpdateRequest) {
        cartMapper.updateCartItemQuantity(cartItemUpdateRequest);
    }
}
