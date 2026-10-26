package com.ecommApp.ecomm_app.inventory.repository;

import com.ecommApp.ecomm_app.common.enums.ReservationStatus;
import com.ecommApp.ecomm_app.inventory.entity.InventoryReservation;
import com.ecommApp.ecomm_app.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryReservationRepository
        extends JpaRepository<InventoryReservation, Long> {

//    List<InventoryReservation> findByOrderId(Long orderId);
//
//    List<InventoryReservation> findByOrder(Order order);
//
//    List<InventoryReservation> findByOrderAndStatus(
//            Order order,
//            ReservationStatus status
//    );


    List<InventoryReservation> findByPaymentId(Long paymentId);
}