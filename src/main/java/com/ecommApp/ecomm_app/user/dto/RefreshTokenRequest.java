package com.ecommApp.ecomm_app.user.dto;

public record RefreshTokenRequest(
        String accessToken,
        String refreshToken
) {
}
