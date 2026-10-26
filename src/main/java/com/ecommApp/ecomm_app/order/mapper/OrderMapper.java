package com.ecommApp.ecomm_app.order.mapper;

import com.ecommApp.ecomm_app.order.dto.OrderResponse;
import com.ecommApp.ecomm_app.order.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {
                OrderAddressMapper.class,
                OrderItemMapper.class
        }
)
public interface OrderMapper {

    @Mapping(source = "shippingAddress", target = "shippingAddress")
    @Mapping(source = "items", target = "items")
    OrderResponse toResponse(Order order);
}
