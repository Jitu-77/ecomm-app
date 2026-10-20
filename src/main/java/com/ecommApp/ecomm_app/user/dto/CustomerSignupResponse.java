package com.ecommApp.ecomm_app.user.dto;

import com.ecommApp.ecomm_app.common.enums.UserRole;

public record  CustomerSignupResponse(
        Long id,
        String email,
        String firstName,
        String lastName,
        String mobile,
        UserRole role
) {
}
