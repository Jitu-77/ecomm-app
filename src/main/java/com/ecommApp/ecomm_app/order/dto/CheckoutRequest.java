package com.ecommApp.ecomm_app.order.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CheckoutRequest(

        Long orderId,

        @NotNull(message = "Address ID is required")
        Long addressId,

        @NotEmpty(message = "At least one cart item is required")
        List<Long> cartItemIds
) {
}
