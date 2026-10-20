package com.ecommApp.ecomm_app.order.repository;

import com.ecommApp.ecomm_app.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findByUserId(Long userId);
}