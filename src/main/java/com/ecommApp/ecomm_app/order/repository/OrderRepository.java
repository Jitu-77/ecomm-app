package com.ecommApp.ecomm_app.order.repository;

import com.ecommApp.ecomm_app.order.entity.Order;
import com.ecommApp.ecomm_app.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserOrderByCreatedAtDesc(User user);

    Optional<Order> findByIdAndUser(Long id, User user);

    Optional<Order> findByOrderNumber(String orderNumber);
}