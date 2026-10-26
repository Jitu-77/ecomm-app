package com.ecommApp.ecomm_app.payment.services;

import com.ecommApp.ecomm_app.common.enums.CartItemStatus;
import com.ecommApp.ecomm_app.common.enums.PaymentStatus;
import com.ecommApp.ecomm_app.common.enums.ReservationStatus;
import com.ecommApp.ecomm_app.inventory.entity.InventoryReservation;
import com.ecommApp.ecomm_app.inventory.repository.InventoryReservationRepository;
import com.ecommApp.ecomm_app.payment.entity.Payment;
import com.ecommApp.ecomm_app.payment.entity.PaymentCartItem;

import com.ecommApp.ecomm_app.payment.repository.PaymentCartItemRepository;
import com.ecommApp.ecomm_app.payment.repository.PaymentRepository;
import com.ecommApp.ecomm_app.inventory.entity.Inventory;
import com.ecommApp.ecomm_app.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentExpiryService {

    private final PaymentRepository paymentRepository;
    private final PaymentCartItemRepository paymentCartItemRepository;
    private final InventoryReservationRepository inventoryReservationRepository;
    private final InventoryRepository inventoryRepository;

    @Transactional
    public void expirePayment(Long paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElse(null);

        // Payment does not exist
        if (payment == null) {
            return;
        }

        // Payment was already processed by another flow
        if (payment.getStatus() != PaymentStatus.PENDING) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        // Payment has not actually expired yet
        if (!payment.getExpiresAt().isBefore(now)) {
            return;
        }

        /*
         * Release inventory reservations
         */
        List<InventoryReservation> reservations =
                inventoryReservationRepository.findByPaymentId(payment.getId());

        for (InventoryReservation reservation : reservations) {

            // Only release active reservations
            if (reservation.getStatus() != ReservationStatus.RESERVED) {
                continue;
            }

            Inventory inventory = inventoryRepository
                    .findByProductId(reservation.getProduct().getId())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Inventory not found for product: "
                                            + reservation.getProduct().getId()
                            )
                    );

            // Return reserved quantity back to available stock
            inventory.setAvailableQty(
                    inventory.getAvailableQty()
                            + reservation.getQuantity()
            );

            // Reduce reserved quantity
            inventory.setReservedQty(
                    inventory.getReservedQty()
                            - reservation.getQuantity()
            );

            // Mark reservation as released
            reservation.setStatus(ReservationStatus.RELEASED);

            inventoryRepository.save(inventory);
            inventoryReservationRepository.save(reservation);
        }

        /*
         * Reset cart items
         */
        List<PaymentCartItem> paymentCartItems =
                paymentCartItemRepository.findByPaymentId(payment.getId());

        for (PaymentCartItem paymentCartItem : paymentCartItems) {

            if (paymentCartItem.getCartItem().getStatus()
                    == CartItemStatus.PAYMENT_PROCESSING) {

                paymentCartItem.getCartItem()
                        .setStatus(CartItemStatus.ACTIVE);
            }
        }

        /*
         * Mark payment as expired
         */
        payment.setStatus(PaymentStatus.EXPIRED);

        paymentRepository.save(payment);
    }
}