package com.ecommApp.ecomm_app.carts.repository;

import com.ecommApp.ecomm_app.carts.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(Long userId);
}