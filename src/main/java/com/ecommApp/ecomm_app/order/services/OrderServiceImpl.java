package com.ecommApp.ecomm_app.order.services;

import com.ecommApp.ecomm_app.carts.entity.CartItem;
import com.ecommApp.ecomm_app.common.enums.CartItemStatus;
import com.ecommApp.ecomm_app.common.enums.OrderStatus;
import com.ecommApp.ecomm_app.common.enums.PaymentStatus;
import com.ecommApp.ecomm_app.common.enums.ReservationStatus;
import com.ecommApp.ecomm_app.common.exceptions.PaymentRelatedException;
import com.ecommApp.ecomm_app.common.exceptions.ResourceNotFoundException;
import com.ecommApp.ecomm_app.inventory.entity.Inventory;
import com.ecommApp.ecomm_app.inventory.entity.InventoryReservation;
import com.ecommApp.ecomm_app.inventory.repository.InventoryRepository;
import com.ecommApp.ecomm_app.inventory.repository.InventoryReservationRepository;
import com.ecommApp.ecomm_app.order.entity.Order;
import com.ecommApp.ecomm_app.order.entity.OrderAddress;
import com.ecommApp.ecomm_app.order.entity.OrderItem;
import com.ecommApp.ecomm_app.order.repository.OrderAddressRepository;
import com.ecommApp.ecomm_app.order.repository.OrderItemRepository;
import com.ecommApp.ecomm_app.order.repository.OrderRepository;
import com.ecommApp.ecomm_app.payment.entity.Payment;
import com.ecommApp.ecomm_app.payment.entity.PaymentCartItem;
import com.ecommApp.ecomm_app.payment.repository.PaymentCartItemRepository;
import com.ecommApp.ecomm_app.payment.repository.PaymentRepository;
import com.ecommApp.ecomm_app.user.auth.SecurityUtils;
import com.ecommApp.ecomm_app.user.entity.Address;
import com.ecommApp.ecomm_app.user.entity.User;
import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final PaymentRepository paymentRepository;
    private final PaymentCartItemRepository paymentCartItemRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryReservationRepository reservationRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderAddressRepository orderAddressRepository;


    // =========================================================
    // SUCCESSFUL PAYMENT
    // =========================================================

    @Override
    @Transactional
    public Order processSuccessfulPayment(String paymentReference) {

        // 1. Find Payment
        Payment payment = paymentRepository
                .findByPaymentReference(paymentReference)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found: " + paymentReference
                        ));

        // 2. Idempotency / already processed
        if (payment.getStatus() == PaymentStatus.SUCCESS
                && payment.getOrder() != null) {

            return payment.getOrder();
        }

        // 3. Payment must currently be PENDING
        if (payment.getStatus() != PaymentStatus.PENDING) {

            throw new PaymentRelatedException(
                    "Payment cannot be processed as SUCCESS. Current status: "
                            + payment.getStatus()
            );
        }

        // 4. Get PaymentCartItems
        List<PaymentCartItem> paymentCartItems =
                paymentCartItemRepository
                        .findByPaymentId(payment.getId());

        if (paymentCartItems.isEmpty()) {

            throw new ResourceNotFoundException(
                    "No payment cart items found for payment: "
                            + paymentReference
            );
        }

        // 5. Get Inventory Reservations
        List<InventoryReservation> reservations =
                reservationRepository
                        .findByPaymentId(payment.getId());

        if (reservations.isEmpty()) {

            throw new ResourceNotFoundException(
                    "No inventory reservations found for payment: "
                            + paymentReference
            );
        }

        // 6. Find User from CartItem
        CartItem firstCartItem =
                paymentCartItems.get(0).getCartItem();

        User user =
                firstCartItem.getCart().getUser();

        // 7. Create Order
        Order order = Order.builder()
                .user(user)
                .orderNumber("ORD-" + UUID.randomUUID())
                .status(OrderStatus.PAYMENT_SUCCESS)
                .totalAmount(payment.getAmount())
                .build();

        order = orderRepository.save(order);

        // 8. Create OrderItems
        for (PaymentCartItem paymentCartItem : paymentCartItems) {

            CartItem cartItem =
                    paymentCartItem.getCartItem();

            Integer quantity =
                    paymentCartItem.getQuantity();

            BigDecimal unitPrice =
                    paymentCartItem.getUnitPrice();

            BigDecimal subtotal =
                    unitPrice.multiply(
                            BigDecimal.valueOf(quantity)
                    );

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .cartItem(cartItem)
                    .subtotal(subtotal)
                    .build();

            orderItemRepository.save(orderItem);

            // Cart item is now part of a completed order
            cartItem.setStatus(CartItemStatus.COMPLETED);
        }

        // 9. Create OrderAddress snapshot
        Address address = payment.getAddress();

        OrderAddress orderAddress = OrderAddress.builder()
                .order(order)
                .street(address.getStreet())
                .city(address.getCity())
                .state(address.getState())
                .country(address.getCountry())
                .zipcode(address.getZipcode())
                .build();

        orderAddressRepository.save(orderAddress);

        // 10. Commit Inventory Reservations
        for (InventoryReservation reservation : reservations) {

            Inventory inventory =
                    inventoryRepository
                            .findByProductIdForUpdate(
                                    reservation.getProduct().getId()
                            )
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Inventory not found for product: "
                                                    + reservation.getProduct().getId()
                                    ));

            // availableQty was already reduced during checkout.
            // We only remove the reservation now.
            inventory.setReservedQty(
                    inventory.getReservedQty()
                            - reservation.getQuantity()
            );

            reservation.setStatus(
                    ReservationStatus.COMMITTED
            );
        }

        // 11. Payment is now SUCCESS
        payment.setStatus(PaymentStatus.SUCCESS);

        // 12. Associate Payment with Order
        payment.setOrder(order);

        paymentRepository.save(payment);

        return order;
    }


    // =========================================================
    // FAILED PAYMENT
    // =========================================================

    @Override
    @Transactional
    public void processFailedPayment(String paymentReference) {

        // 1. Find Payment
        Payment payment = paymentRepository
                .findByPaymentReference(paymentReference)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found: " + paymentReference
                        ));

        // 2. Idempotency
        if (payment.getStatus() == PaymentStatus.FAILED) {
            return;
        }

        // 3. Payment must currently be PENDING
        if (payment.getStatus() != PaymentStatus.PENDING) {

            throw new PaymentRelatedException(
                    "Payment cannot be processed as FAILED. Current status: "
                            + payment.getStatus()
            );
        }

        // 4. Find reservations
        List<InventoryReservation> reservations =
                reservationRepository
                        .findByPaymentId(payment.getId());

        // 5. Release Inventory
        for (InventoryReservation reservation : reservations) {

            Inventory inventory =
                    inventoryRepository
                            .findByProductIdForUpdate(
                                    reservation.getProduct().getId()
                            )
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Inventory not found for product: "
                                                    + reservation.getProduct().getId()
                                    ));

            // Give the reserved quantity back
            inventory.setAvailableQty(
                    inventory.getAvailableQty()
                            + reservation.getQuantity()
            );

            // Remove it from reserved quantity
            inventory.setReservedQty(
                    inventory.getReservedQty()
                            - reservation.getQuantity()
            );

            // Reservation is released
            reservation.setStatus(
                    ReservationStatus.RELEASED
            );
        }

        // 6. Reactivate CartItems
        List<PaymentCartItem> paymentCartItems =
                paymentCartItemRepository
                        .findByPaymentId(payment.getId());

        for (PaymentCartItem paymentCartItem : paymentCartItems) {

            CartItem cartItem =
                    paymentCartItem.getCartItem();

            cartItem.setStatus(
                    CartItemStatus.ACTIVE
            );
        }

        // 7. Mark Payment as FAILED
        payment.setStatus(PaymentStatus.FAILED);

        paymentRepository.save(payment);

        // IMPORTANT:
        // No Order is created for FAILED payment.
    }


    // =========================================================
    // GET MY ORDERS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<Order> getMyOrders() {

        User user = SecurityUtils.getCurrentUser();

        return orderRepository
                .findByUserOrderByCreatedAtDesc(user);
    }


    // =========================================================
    // GET MY ORDER
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public Order getMyOrder(Long orderId) {

        User user = SecurityUtils.getCurrentUser();

        return orderRepository
                .findByIdAndUser(orderId, user)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found: " + orderId
                        ));
    }
}
