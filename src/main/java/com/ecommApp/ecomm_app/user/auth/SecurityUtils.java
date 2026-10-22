package com.ecommApp.ecomm_app.user.auth;

import com.ecommApp.ecomm_app.user.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    public static User getCurrentUser() {
        return (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}
