package com.ecommApp.ecomm_app.carts.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CheckoutRequest(

        @NotNull(message = "Address ID is required")
        Long addressId,

        @NotEmpty(message = "At least one cart item is required")
        List<Long> cartItemIds
) {
}