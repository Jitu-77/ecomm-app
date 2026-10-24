package com.ecommApp.ecomm_app.carts.dto;

import com.ecommApp.ecomm_app.common.enums.CartStatus;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
        Long id,
        CartStatus status,
        List<CartItemResponse> items,
        BigDecimal total
) {
}
