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
import java.util.Map;
import java.util.stream.Collectors;

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

        List<Cart> cartList = cartMapper.selectCartList(userId);
        Map<String, List<Cart>> grouped = cartList.stream()
                .collect(Collectors.groupingBy(Cart::getProductId));

        return grouped.values().stream()
                .map(group -> {
                    Cart first = group.get(0);

                    CartResponse cartResponse = new CartResponse();
                    cartResponse.setProductId(first.getProductId());
                    cartResponse.setProductName(first.getProductName());
                    cartResponse.setCategoryName(first.getCategoryName());
                    cartResponse.setPrice(first.getPrice());
                    cartResponse.setShippingPrice(first.getShippingPrice());
                    cartResponse.setImages(first.getImages());
                    cartResponse.setOptionRequired(first.isOptionRequired());



                    // 옵션이 있는 상품
                    List<CartOption> options = group.stream()
                            .filter(v -> {
                                if (v.getOptionId() == null) {
                                    cartResponse.setCartId(v.getCartId());
                                    cartResponse.setQuantity(v.getQuantity());
                                    return false;
                                }
                                return true;
                            })
                            .map(v -> {
                                CartOption opt = new CartOption();
                                opt.setCartId(v.getCartId());
                                opt.setOptionId(v.getOptionId());
                                opt.setOptionName(v.getOptionName());
                                opt.setOptionPrice(v.getOptionPrice());
                                opt.setQuantity(v.getQuantity());
                                return opt;
                            })
                            .collect(Collectors.toList());

                    cartResponse.setOptions(options);

                    return cartResponse;
                })
                .collect(Collectors.toList());
    }

    public void updateCartList(List<CartUpdateRequest> cartUpdateRequestList) {
        for (CartUpdateRequest cartUpdateRequest : cartUpdateRequestList) {
            this.upsertCart(cartUpdateRequest);
        }
    }

    public void deleteCart(CartDeleteRequest cartDeleteRequest) {
        cartMapper.deleteCart(cartDeleteRequest);
    }

    public void deleteCartList(List<String> productIdList) {
        AuthUser authUser = jwtTokenProvider.getUserInfo();
        String userId = authUser.getUserId();

        for (String productId : productIdList) {
            CartDeleteRequest cartDeleteRequest = new CartDeleteRequest();
            cartDeleteRequest.setProductId(productId);
            cartDeleteRequest.setUserId(userId);
            cartMapper.deleteCart(cartDeleteRequest);
        }
    }

    public void updateCartQuantity(CartUpdateRequest cartUpdateRequest) {
        cartMapper.updateCartQuantity(cartUpdateRequest);
    }
}
