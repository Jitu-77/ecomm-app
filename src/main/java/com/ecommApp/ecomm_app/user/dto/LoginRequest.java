package com.ecommApp.ecomm_app.user.dto;

public record LoginRequest(
        String email,
        String password
) {
}
