package com.gwana.server.mapper;

import com.gwana.server.dto.order.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {
    int insertOrder(CreateOrder createOrder);

    void insertOrderItems(@Param("createOrderItems")List<CreateOrderItem> createOrderItems);

    Order selectOrder(OrderSearchRequest orderSearchRequest);

    List<OrderItem> selectOrderItems(OrderSearchRequest orderSearchRequest);
}
