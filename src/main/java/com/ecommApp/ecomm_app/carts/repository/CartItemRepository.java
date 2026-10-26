package com.ecommApp.ecomm_app.carts.repository;

import com.ecommApp.ecomm_app.carts.entity.Cart;
import com.ecommApp.ecomm_app.carts.entity.CartItem;
import com.ecommApp.ecomm_app.common.enums.CartItemStatus;
import com.ecommApp.ecomm_app.products.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT ci
            FROM CartItem ci
            WHERE ci.id IN :cartItemIds
            AND ci.cart.user.id = :userId
            """)
    List<CartItem> findCartItemsForCheckout(
            @Param("cartItemIds") List<Long> cartItemIds,
            @Param("userId") Long userId
    );
    Optional<CartItem> findByCartAndProductAndStatus(
            Cart cart,
            Product product,
            CartItemStatus status
    );

    Optional<CartItem> findByIdAndCartAndStatus(
            Long id,
            Cart cart,
            CartItemStatus status
    );

    List<CartItem> findByCartAndStatus(
            Cart cart,
            CartItemStatus status
    );
}