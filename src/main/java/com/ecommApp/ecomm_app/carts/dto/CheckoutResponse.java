package com.ecommApp.ecomm_app.carts.dto;

import com.ecommApp.ecomm_app.common.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CheckoutResponse(

        Long paymentId,

        String paymentReference,

        BigDecimal amount,

        PaymentStatus status,

        LocalDateTime expiresAt
) {
}