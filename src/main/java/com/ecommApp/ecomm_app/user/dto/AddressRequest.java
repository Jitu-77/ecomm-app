package com.ecommApp.ecomm_app.user.dto;

public record AddressRequest(
        String street,
        String city,
        String state,
        String country,
        String zipcode,
        String addressType,
        Boolean isDefault
) {
}
