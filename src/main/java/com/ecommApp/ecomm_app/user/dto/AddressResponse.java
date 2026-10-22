package com.ecommApp.ecomm_app.user.dto;

import java.time.LocalDateTime;

public record AddressResponse(
        Long id,
        String street,
        String city,
        String state,
        String country,
        String zipcode,
        String addressType,
        Boolean isDefault,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
