package com.ecommApp.ecomm_app.payment.repository;

import com.ecommApp.ecomm_app.payment.entity.PaymentCartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentCartItemRepository
        extends JpaRepository<PaymentCartItem, Long> {

    List<PaymentCartItem> findByPaymentId(Long paymentId);
}
