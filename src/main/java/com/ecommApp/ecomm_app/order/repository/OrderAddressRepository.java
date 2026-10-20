package com.ecommApp.ecomm_app.order.repository;

import com.ecommApp.ecomm_app.order.entity.OrderAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderAddressRepository
        extends JpaRepository<OrderAddress, Long> {

    Optional<OrderAddress> findByOrderId(Long orderId);
}