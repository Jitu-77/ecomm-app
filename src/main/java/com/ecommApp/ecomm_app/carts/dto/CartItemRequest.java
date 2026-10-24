package com.ecommApp.ecomm_app.carts.dto;

public record CartItemRequest(
        Long productId,
        Integer quantity
) {
}