package com.ecommApp.ecomm_app.order.dto;

import com.ecommApp.ecomm_app.common.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,

        String orderNumber,

        OrderStatus status,

        BigDecimal totalAmount,

        OrderAddressResponse shippingAddress,

        List<OrderItemResponse> items,

        LocalDateTime expiresAt,

        LocalDateTime createdAt,

        LocalDateTime updatedAt
) {
}
