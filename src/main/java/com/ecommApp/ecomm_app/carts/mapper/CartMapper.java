package com.ecommApp.ecomm_app.carts.mapper;

import com.ecommApp.ecomm_app.carts.dto.CartResponse;
import com.ecommApp.ecomm_app.carts.entity.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = CartItemMapper.class
)
public interface CartMapper {

    @Mapping(target = "total", ignore = true)
    CartResponse toResponse(Cart cart);
}
