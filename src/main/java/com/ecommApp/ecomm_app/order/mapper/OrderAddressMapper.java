package com.ecommApp.ecomm_app.order.mapper;

import com.ecommApp.ecomm_app.order.dto.OrderAddressResponse;
import com.ecommApp.ecomm_app.order.entity.OrderAddress;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderAddressMapper {

    OrderAddressResponse toResponse(OrderAddress orderAddress);
}