package com.ecommApp.ecomm_app.payment.repository;

import com.ecommApp.ecomm_app.common.enums.PaymentStatus;
import com.ecommApp.ecomm_app.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository
        extends JpaRepository<Payment, Long> {

    Optional<Payment> findByPaymentReference(String paymentReference);

    List<Payment> findByStatusAndExpiresAtBefore(
            PaymentStatus status,
            LocalDateTime now
    );

    }