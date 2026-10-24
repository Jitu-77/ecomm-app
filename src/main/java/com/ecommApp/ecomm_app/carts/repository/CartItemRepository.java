package com.ecommApp.ecomm_app.carts.repository;

import com.ecommApp.ecomm_app.carts.entity.Cart;
import com.ecommApp.ecomm_app.carts.entity.CartItem;
import com.ecommApp.ecomm_app.products.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartAndProduct(
            Cart cart,
            Product product
    );

    Optional<CartItem> findByIdAndCart(
            Long id,
            Cart cart
    );
}