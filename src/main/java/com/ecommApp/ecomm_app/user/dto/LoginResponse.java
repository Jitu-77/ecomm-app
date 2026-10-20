package com.ecommApp.ecomm_app.user.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        Long userId,
        String email,
        String firstName,
        String lastName
) {
}

