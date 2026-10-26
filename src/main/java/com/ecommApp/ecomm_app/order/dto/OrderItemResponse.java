package com.ecommApp.ecomm_app.order.dto;

import java.math.BigDecimal;

public record OrderItemResponse(

        Long id,

        Long cartItemId,

        Long productId,

        String productName,

        BigDecimal unitPrice,

        Integer quantity,

        BigDecimal subtotal

) {
}