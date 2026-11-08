package com.ecommApp.ecomm_app.order.services;

import com.ecommApp.ecomm_app.order.entity.Order;

import java.util.List;

public interface OrderService {


    Order processSuccessfulPayment(String paymentReference);

    List<Order> getMyOrders();

    Order getMyOrder(Long orderId);

    void processFailedPayment(String ref);
}