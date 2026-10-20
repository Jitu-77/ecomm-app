package com.ecommApp.ecomm_app.user.dto;

import com.ecommApp.ecomm_app.common.enums.UserRole;

public record CustomerSignupRequest(
        String email,
        String password,
        String firstName,
        String lastName,
        String mobile,
        UserRole role
) {
}
