package com.ecommApp.ecomm_app.inventory.repository;

import com.ecommApp.ecomm_app.inventory.entity.InventoryReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryReservationRepository
        extends JpaRepository<InventoryReservation, Long> {

    List<InventoryReservation> findByOrderId(Long orderId);
}