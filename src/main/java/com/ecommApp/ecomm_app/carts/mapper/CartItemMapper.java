package com.ecommApp.ecomm_app.carts.mapper;

import com.ecommApp.ecomm_app.carts.dto.CartItemResponse;
import com.ecommApp.ecomm_app.carts.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartItemMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(target = "unitPrice", source = "unitPrice")
    @Mapping(target = "subtotal", ignore = true)
    CartItemResponse toResponse(CartItem cartItem);
}
