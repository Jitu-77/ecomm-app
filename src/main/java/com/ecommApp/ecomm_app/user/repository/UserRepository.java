package com.ecommApp.ecomm_app.user.repository;

import com.ecommApp.ecomm_app.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
}