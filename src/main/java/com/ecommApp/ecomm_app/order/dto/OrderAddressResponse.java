package com.ecommApp.ecomm_app.order.dto;

public record OrderAddressResponse(
        Long id,

        String street,

        String city,

        String state,

        String country,

        String zipcode
) {
}
