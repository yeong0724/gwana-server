package com.gwana.server.mapper;

import com.gwana.server.dto.cart.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CartMapper {
    List<Cart> selectCartList(String userId);

    List<CartItem> selectCartItemList(Long cartId);

    Long selectCartByUserAndProduct(@Param("productId")Long productId, @Param("userId")String userId);

    void insertCart(UpsertCartRequest upsertCartRequest);

    void upsertCartItem(UpsertCartItemRequest upsertCartItemRequest);

    void deleteCartItem(CartItemDeleteRequest cartItemDeleteRequest);

    void deleteCart(CartDeleteRequest cartDeleteRequest);

    void deleteCartItemByCartId(CartDeleteRequest cartDeleteRequest);

    void updateCartItemQuantity(CartItemUpdateRequest cartItemUpdateRequest);
}
