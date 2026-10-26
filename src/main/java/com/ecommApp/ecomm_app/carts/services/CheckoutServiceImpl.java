package com.ecommApp.ecomm_app.carts.services;

import com.ecommApp.ecomm_app.carts.dto.CheckoutRequest;
import com.ecommApp.ecomm_app.carts.dto.CheckoutResponse;
import com.ecommApp.ecomm_app.carts.entity.CartItem;
import com.ecommApp.ecomm_app.carts.repository.CartItemRepository;
import com.ecommApp.ecomm_app.common.enums.CartItemStatus;
import com.ecommApp.ecomm_app.common.enums.PaymentStatus;
import com.ecommApp.ecomm_app.common.enums.ReservationStatus;
import com.ecommApp.ecomm_app.inventory.entity.Inventory;
import com.ecommApp.ecomm_app.inventory.entity.InventoryReservation;
import com.ecommApp.ecomm_app.inventory.repository.InventoryRepository;
import com.ecommApp.ecomm_app.inventory.repository.InventoryReservationRepository;
import com.ecommApp.ecomm_app.payment.entity.Payment;
import com.ecommApp.ecomm_app.payment.entity.PaymentCartItem;
import com.ecommApp.ecomm_app.payment.repository.PaymentCartItemRepository;
import com.ecommApp.ecomm_app.payment.repository.PaymentRepository;
import com.ecommApp.ecomm_app.user.auth.SecurityUtils;
import com.ecommApp.ecomm_app.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {

    private final CartItemRepository cartItemRepository;
    private final InventoryRepository inventoryRepository;
    private final PaymentRepository paymentRepository;
    private final InventoryReservationRepository reservationRepository;
    private final PaymentCartItemRepository paymentCartItemRepository;

    @Override
    @Transactional
    public CheckoutResponse checkout(CheckoutRequest request) {

        // 1. Get current user
        User user = SecurityUtils.getCurrentUser();

        // 2. Lock selected cart items
        List<CartItem> cartItems =
                cartItemRepository.findCartItemsForCheckout(
                        request.cartItemIds(),
                        user.getId()
                );

        // 3. Validate all requested items were found
        if (cartItems.size() != request.cartItemIds().size()) {
            throw new RuntimeException(
                    "One or more cart items are invalid"
            );
        }

        // 4. Validate CartItem status
        for (CartItem cartItem : cartItems) {

            if (cartItem.getStatus() != CartItemStatus.ACTIVE) {
                throw new RuntimeException(
                        "Cart item " + cartItem.getId()
                                + " is not available for checkout"
                );
            }
        }

        // 5. Calculate total
        BigDecimal totalAmount = cartItems.stream()
                .map(item ->
                        item.getUnitPrice()
                                .multiply(
                                        BigDecimal.valueOf(item.getQuantity())
                                )
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 6. Reserve inventory
        for (CartItem cartItem : cartItems) {

            Inventory inventory =
                    inventoryRepository
                            .findByProductIdForUpdate(
                                    cartItem.getProduct().getId()
                            )
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Inventory not found for product "
                                                    + cartItem.getProduct().getId()
                                    )
                            );

            if (inventory.getAvailableQty()
                    < cartItem.getQuantity()) {

                throw new RuntimeException(
                        "Insufficient stock for product "
                                + cartItem.getProduct().getId()
                );
            }

            inventory.setAvailableQty(
                    inventory.getAvailableQty()
                            - cartItem.getQuantity()
            );

            inventory.setReservedQty(
                    inventory.getReservedQty()
                            + cartItem.getQuantity()
            );
        }

        // 7. Move CartItems to PAYMENT_PROCESSING
        for (CartItem cartItem : cartItems) {
            cartItem.setStatus(CartItemStatus.PAYMENT_PROCESSING);
        }

        // 8. Create Payment
        LocalDateTime expiresAt =
                LocalDateTime.now().plusMinutes(4);

        Payment payment = Payment.builder()
                .paymentReference(
                        "PAY-" + UUID.randomUUID()
                )
                .provider("PAYMENT_SIMULATOR")
                .providerPaymentId(
                        "SIM-" + UUID.randomUUID()
                )
                .transactionRef(
                        "TXN-" + UUID.randomUUID()
                )
                .paymentType("ONLINE")
                .status(PaymentStatus.PENDING)
                .amount(totalAmount)
                .expiresAt(expiresAt)
                .build();

        payment = paymentRepository.save(payment);

        // 9. Create PaymentCartItems
        for (CartItem cartItem : cartItems) {

            PaymentCartItem paymentCartItem =
                    PaymentCartItem.builder()
                            .payment(payment)
                            .cartItem(cartItem)
                            .quantity(cartItem.getQuantity())
                            .unitPrice(cartItem.getUnitPrice())
                            .build();

            paymentCartItemRepository.save(paymentCartItem);
        }


        // 10. Create inventory reservations
        for (CartItem cartItem : cartItems) {

            InventoryReservation reservation =
                    InventoryReservation.builder()
                            .payment(payment)
                            .product(cartItem.getProduct())
                            .quantity(cartItem.getQuantity())
                            .status(ReservationStatus.RESERVED)
                            .expiresAt(expiresAt)
                            .build();

            reservationRepository.save(reservation);
        }

        // 11. Return payment details
        return new CheckoutResponse(
                payment.getId(),
                payment.getPaymentReference(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getExpiresAt()
        );
    }
}
