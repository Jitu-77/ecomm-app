package com.ecommApp.ecomm_app.order.mapper;

import com.ecommApp.ecomm_app.order.dto.OrderItemResponse;
import com.ecommApp.ecomm_app.order.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(source = "cartItem.id", target = "cartItemId")
    @Mapping(source = "cartItem.product.id", target = "productId")
    @Mapping(source = "cartItem.product.name", target = "productName")
    @Mapping(source = "cartItem.unitPrice", target = "unitPrice")
    @Mapping(source = "cartItem.quantity", target = "quantity")
    OrderItemResponse toResponse(OrderItem orderItem);
}