package com.gwana.server.mapper;

import com.gwana.server.dto.cart.Cart;
import com.gwana.server.dto.cart.CartDeleteRequest;
import com.gwana.server.dto.cart.CartUpdateRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CartMapper {
    List<Cart> selectCartList(String userId);

    void upsertCart(CartUpdateRequest cartUpdateRequest);

    void deleteCart(CartDeleteRequest cartDeleteRequest);

    void updateCartQuantity(CartUpdateRequest cartUpdateRequest);
}
