//package com.ecommApp.ecomm_app.order.services;
//
//import com.ecommApp.ecomm_app.carts.entity.Cart;
//import com.ecommApp.ecomm_app.carts.entity.CartItem;
//import com.ecommApp.ecomm_app.carts.repository.CartItemRepository;
//import com.ecommApp.ecomm_app.carts.repository.CartRepository;
//import com.ecommApp.ecomm_app.common.enums.*;
//import com.ecommApp.ecomm_app.common.exceptions.PaymentRelatedException;
//import com.ecommApp.ecomm_app.common.idempotency.entity.IdempotencyRecord;
//import com.ecommApp.ecomm_app.common.idempotency.repository.IdempotencyRecordRepository;
//import com.ecommApp.ecomm_app.inventory.entity.Inventory;
//import com.ecommApp.ecomm_app.inventory.entity.InventoryReservation;
//import com.ecommApp.ecomm_app.inventory.repository.InventoryRepository;
//import com.ecommApp.ecomm_app.inventory.repository.InventoryReservationRepository;
//import com.ecommApp.ecomm_app.order.dto.CheckoutRequest;
//import com.ecommApp.ecomm_app.order.dto.OrderResponse;
//import com.ecommApp.ecomm_app.order.entity.Order;
//import com.ecommApp.ecomm_app.order.entity.OrderAddress;
//import com.ecommApp.ecomm_app.order.entity.OrderItem;
//import com.ecommApp.ecomm_app.order.mapper.OrderMapper;
//import com.ecommApp.ecomm_app.order.repository.OrderRepository;
//import com.ecommApp.ecomm_app.payment.entity.Payment;
//import com.ecommApp.ecomm_app.payment.repository.PaymentRepository;
//import com.ecommApp.ecomm_app.user.auth.SecurityUtils;
//import com.ecommApp.ecomm_app.user.entity.Address;
//import com.ecommApp.ecomm_app.user.entity.User;
//import com.ecommApp.ecomm_app.user.repository.AddressRepository;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.nio.charset.StandardCharsets;
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class OrderServiceImpl implements OrderService {
//
//    private final OrderRepository orderRepository;
//    private final OrderMapper orderMapper;
//    private final InventoryRepository inventoryRepository;
//    private final AddressRepository addressRepository;
//    private final CartItemRepository cartItemRepository;
//    private final CartRepository cartRepository;
//    private final IdempotencyRecordRepository idempotencyRecordRepository;
//    private final InventoryReservationRepository inventoryReservationRepository;
//    private final PaymentRepository paymentRepository;
////    @Override
////    @Transactional
////    public OrderResponse checkout(CheckoutRequest request) {
////
////        User currentUser = SecurityUtils.getCurrentUser();
////
////        // 1. Validate cart
////        Cart cart = validateCart(currentUser);
////
////        // 2. Validate selected cart items
////        List<CartItem> cartItems =
////                validateCartItems(
////                        request.cartItemIds(),
////                        cart
////                );
////
////        // 3. Validate address ownership
////        Address address =
////                validateAddress(
////                        request.addressId(),
////                        currentUser
////                );
////
////        // 4. Check existing order if orderId is provided
////        if (request.orderId() != null) {
////
////            Optional<Order> existingOrder =
////                    orderRepository.findByIdAndUser(
////                            request.orderId(),
////                            currentUser
////                    );
////
////            if (existingOrder.isPresent()) {
////
////                Order order = existingOrder.get();
////
////                if (order.getStatus() == OrderStatus.PENDING_PAYMENT) {
////                    throw new RuntimeException(
////                            "Order is still pending payment"
////                    );
////                }
////
////                if (order.getStatus() == OrderStatus.CANCELLED
////                        || order.getStatus() == OrderStatus.CONFIRMED) {
////
////                    throw new RuntimeException(
////                            "Order is not eligible for retry"
////                    );
////                }
////
////                // PAYMENT_FAILED / EXPIRED
////                // Continue and create a new order.
////            }else{
////
////            }
////        }
////
////        // 5. Lock inventory
////        // 6. Validate available quantity
////        // 7. Reserve inventory
////        reserveInventory(cartItems);
////
////        // Calculate total
////        BigDecimal totalAmount =
////                calculateTotalAmount(cartItems);
////
////        // 8. Create Order
////        Order order =
////                createOrder(
////                        currentUser,
////                        totalAmount
////                );
////
////        // 9. Create OrderItems
////        createOrderItems(
////                order,
////                cartItems
////        );
////
////        // 10. Create OrderAddress
////        createOrderAddress(
////                order,
////                address
////        );
////
////        // 11. Create IdempotencyRecord
////        createIdempotencyRecord(
////                order,
////                currentUser,
////                request
////        );
////
////        // 12. Return OrderResponse
////        return orderMapper.toResponse(order);
////    }
//
//    @Override
//    @Transactional
//    public OrderResponse checkout(CheckoutRequest request) {
//
//        User currentUser = SecurityUtils.getCurrentUser();
//
//        // 1. Validate checkout request
//        Cart cart = validateCart(currentUser);
//
//        List<CartItem> cartItems =
//                validateCartItems(
//                        request.cartItemIds(),
//                        cart
//                );
//
//        Address address =
//                validateAddress(
//                        request.addressId(),
//                        currentUser
//                );
//
//        // 2. Retry existing order
//        if (request.orderId() != null) {
//
//            Optional<Order> existingOrder =
//                    orderRepository.findByIdAndUser(
//                            request.orderId(),
//                            currentUser
//                    );
//
//            if (existingOrder.isPresent()) {
//
//                return retryExistingOrder(
//                        existingOrder.get(),
//                        cartItems
//                );
//            }
//        }
//
//        // 3. Fresh checkout
//        return createNewOrder(
//                currentUser,
//                cartItems,
//                address,
//                request
//        );
//    }
//
//
//    private OrderResponse retryExistingOrder(
//            Order order,
//            List<CartItem> cartItems
//    ) {
//
//        // Order is still waiting for payment
//        if (order.getStatus() == OrderStatus.PENDING_PAYMENT) {
//
//            throw new PaymentRelatedException(
//                    "Order is still pending payment"
//            );
//        }
//
//        // These orders cannot be retried
//        if (order.getStatus() == OrderStatus.CONFIRMED
//                || order.getStatus() == OrderStatus.CANCELLED) {
//
//            throw new PaymentRelatedException(
//                    "Order is not eligible for retry"
//            );
//        }
//
//        // Only PAYMENT_FAILED and EXPIRED
//        if (order.getStatus() != OrderStatus.PAYMENT_FAILED
//                && order.getStatus() != OrderStatus.EXPIRED) {
//
//            throw new PaymentRelatedException(
//                    "Order is not eligible for retry"
//            );
//        }
//
//        // Reset order for new payment attempt
//        order.setStatus(OrderStatus.PENDING_PAYMENT);
//
//        order.setExpiresAt(
//                LocalDateTime.now().plusMinutes(4)
//        );
//
//        // Reserve inventory again
//        reserveInventory(
//                order,
//                cartItems
//        );
//
//        // Create a NEW payment attempt
//        createPayment(
//                order,
//                order.getTotalAmount()
//        );
//
//        // Idempotency becomes PROCESSING again
//        updateIdempotencyRecord(order);
//
//        return orderMapper.toResponse(order);
//    }
//
//    private OrderResponse createNewOrder(
//            User currentUser,
//            List<CartItem> cartItems,
//            Address address,
//            CheckoutRequest request
//    ) {
//
//        // 1. Calculate total
//        BigDecimal totalAmount =
//                calculateTotalAmount(cartItems);
//
//        // 2. Create Order
//        Order order =
//                createOrder(
//                        currentUser,
//                        totalAmount
//                );
//
//        // 3. Reserve inventory
//        reserveInventory(
//                order,
//                cartItems
//        );
//
//        // 4. Create OrderItems
//        createOrderItems(
//                order,
//                cartItems
//        );
//
//        // 5. Create OrderAddress
//        createOrderAddress(
//                order,
//                address
//        );
//        // 6. Create Payment in our db
//        createPayment(
//                order,
//                totalAmount
//        );
//
//        // 7. Create IdempotencyRecord
//        createIdempotencyRecord(
//                order,
//                currentUser,
//                request
//        );
//
//        return orderMapper.toResponse(order);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<OrderResponse> getMyOrders() {
//
//        User currentUser = SecurityUtils.getCurrentUser();
//
//        List<Order> orders =
//                orderRepository.findByUserOrderByCreatedAtDesc(currentUser);
//
//        return orders.stream()
//                .map(orderMapper::toResponse)
//                .toList();
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public OrderResponse getOrderById(Long orderId) {
//
//        User currentUser = SecurityUtils.getCurrentUser();
//
//        Order order = orderRepository
//                .findByIdAndUser(orderId, currentUser)
//                .orElseThrow(() ->
//                        new RuntimeException("Order not found")
//                );
//
//        return orderMapper.toResponse(order);
//    }
//    private BigDecimal calculateTotalAmount(
//            List<CartItem> cartItems
//    ) {
//
//        return cartItems.stream()
//                .map(item ->
//                        item.getUnitPrice()
//                                .multiply(
//                                        BigDecimal.valueOf(
//                                                item.getQuantity()
//                                        )
//                                )
//                )
//                .reduce(
//                        BigDecimal.ZERO,
//                        BigDecimal::add
//                );
//    }
//
//    private OrderAddress createOrderAddress(
//            Order order,
//            Address address
//    ) {
//
//        OrderAddress orderAddress =
//                OrderAddress.builder()
//                        .order(order)
//                        .street(address.getStreet())
//                        .city(address.getCity())
//                        .state(address.getState())
//                        .country(address.getCountry())
//                        .zipcode(address.getZipcode())
//                        .build();
//
//        order.setShippingAddress(orderAddress);
//
//        return orderAddress;
//    }
//
//    private List<OrderItem> createOrderItems(
//            Order order,
//            List<CartItem> cartItems
//    ) {
//
//        List<OrderItem> orderItems =
//                cartItems.stream()
//                        .map(cartItem -> {
//
//                            BigDecimal subtotal =
//                                    cartItem.getUnitPrice()
//                                            .multiply(
//                                                    BigDecimal.valueOf(
//                                                            cartItem.getQuantity()
//                                                    )
//                                            );
//
//                            return OrderItem.builder()
//                                    .order(order)
//                                    .cartItem(cartItem)
//                                    .subtotal(subtotal)
//                                    .build();
//                        })
//                        .toList();
//
//        order.getItems().addAll(orderItems);
//
//        return orderItems;
//    }
//
//    private Order createOrder(
//            User currentUser,
//            BigDecimal totalAmount
//    ) {
//
//        Order order = Order.builder()
//                .user(currentUser)
//                .orderNumber(generateOrderNumber())
//                .status(OrderStatus.PENDING_PAYMENT)
//                .totalAmount(totalAmount)
//                .expiresAt(
//                        LocalDateTime.now().plusMinutes(4)
//                )
//                .build();
//
//        return orderRepository.save(order);
//    }
//
//
////    private void reserveInventory(
////            Order order,
////            List<CartItem> cartItems
////    ) {
////
////        List<Inventory> inventories =
////                new ArrayList<>();
////
////        // Step 5 + Step 6
////        for (CartItem cartItem : cartItems) {
////
////            Inventory inventory =
////                    inventoryRepository
////                            .findByProductIdForUpdate(
////                                    cartItem.getProduct().getId()
////                            )
////                            .orElseThrow(() ->
////                                    new RuntimeException(
////                                            "Inventory not found for product: "
////                                                    + cartItem.getProduct().getName()
////                                    )
////                            );
////
////            if (inventory.getAvailableQty()
////                    < cartItem.getQuantity()) {
////
////                throw new RuntimeException(
////                        "Insufficient stock for product: "
////                                + cartItem.getProduct().getName()
////                );
////            }
////
////            inventories.add(inventory);
////        }
////
////        // Step 7
////        for (int i = 0; i < cartItems.size(); i++) {
////
////            CartItem cartItem = cartItems.get(i);
////            Inventory inventory = inventories.get(i);
////
////            inventory.setAvailableQty(
////                    inventory.getAvailableQty()
////                            - cartItem.getQuantity()
////            );
////
////            inventory.setReservedQty(
////                    inventory.getReservedQty()
////                            + cartItem.getQuantity()
////            );
////        }
////
////        inventoryRepository.saveAll(inventories);
////    }
//
//
//
//    private void reserveInventory(
//            Order order,
//            List<CartItem> cartItems
//    ) {
//
//        List<Inventory> inventories =
//                new ArrayList<>();
//
//        // 1. Lock inventory and validate stock
//        for (CartItem cartItem : cartItems) {
//
//            Inventory inventory =
//                    inventoryRepository
//                            .findByProductIdForUpdate(
//                                    cartItem.getProduct().getId()
//                            )
//                            .orElseThrow(() ->
//                                    new RuntimeException(
//                                            "Inventory not found for product: "
//                                                    + cartItem.getProduct().getName()
//                                    )
//                            );
//
//            if (inventory.getAvailableQty()
//                    < cartItem.getQuantity()) {
//
//                throw new RuntimeException(
//                        "Insufficient stock for product: "
//                                + cartItem.getProduct().getName()
//                );
//            }
//
//            inventories.add(inventory);
//        }
//
//        // 2. Update inventory quantities
//        for (int i = 0; i < cartItems.size(); i++) {
//
//            CartItem cartItem = cartItems.get(i);
//            Inventory inventory = inventories.get(i);
//
//            inventory.setAvailableQty(
//                    inventory.getAvailableQty()
//                            - cartItem.getQuantity()
//            );
//
//            inventory.setReservedQty(
//                    inventory.getReservedQty()
//                            + cartItem.getQuantity()
//            );
//        }
//
//        inventoryRepository.saveAll(inventories);
//
//        // 3. Create inventory reservation records
//        List<InventoryReservation> reservations =
//                new ArrayList<>();
//
//        for (CartItem cartItem : cartItems) {
//
//            InventoryReservation reservation =
//                    InventoryReservation.builder()
//                            .order(order)
//                            .product(cartItem.getProduct())
//                            .quantity(cartItem.getQuantity())
//                            .status(ReservationStatus.RESERVED)
//                            .expiresAt(order.getExpiresAt())
//                            .build();
//
//            reservations.add(reservation);
//        }
//
//        inventoryReservationRepository.saveAll(
//                reservations
//        );
//    }
//
//
//
//    private Address validateAddress(
//            Long addressId,
//            User currentUser
//    ) {
//
//        return addressRepository
//                .findByIdAndUserId(
//                        addressId,
//                        currentUser.getId()
//                )
//                .orElseThrow(() ->
//                        new RuntimeException(
//                                "Address not found"
//                        )
//                );
//    }
//
//    private List<CartItem> validateCartItems(
//            List<Long> cartItemIds,
//            Cart cart
//    ) {
//
//        List<CartItem> activeCartItems =
//                cartItemRepository.findByCartAndStatus(
//                        cart,
//                        CartItemStatus.ACTIVE
//                );
//
//        List<CartItem> selectedItems =
//                activeCartItems.stream()
//                        .filter(item ->
//                                cartItemIds.contains(item.getId())
//                        )
//                        .toList();
//
//        if (selectedItems.size() != cartItemIds.size()) {
//            throw new RuntimeException(
//                    "One or more cart items are invalid"
//            );
//        }
//
//        return selectedItems;
//    }
//    private Cart validateCart(User currentUser) {
//
//        Cart cart = cartRepository
//                .findByUser(currentUser)
//                .orElseThrow(() ->
//                        new RuntimeException("Cart not found")
//                );
//
//        if (cart.getStatus() != CartStatus.ACTIVE) {
//            throw new RuntimeException(
//                    "Cart is not active"
//            );
//        }
//
//        return cart;
//    }
//    private void createIdempotencyRecord(
//            Order order,
//            User currentUser,
//            CheckoutRequest request
//    ) {
//
//        // Generate a deterministic hash of the checkout request.
//        // This helps identify whether the business request data is the same.
//        String requestHash =
//                generateRequestHash(request);
//
//        // Create a unique backend-generated idempotency key.
//        // The client does not need to generate or manage this key.
//        String idempotencyKey =
//                UUID.randomUUID().toString();
//
//        IdempotencyRecord record =
//                IdempotencyRecord.builder()
//                        .user(currentUser)
//                        .order(order)
//                        .idempotencyKey(idempotencyKey)
//                        .requestHash(requestHash)
//
//                        // Checkout has been created but payment
//                        // has not been completed yet.
//                        .status(IdempotencyStatus.PROCESSING)
//
//                        .createdAt(LocalDateTime.now())
//
//                        // Idempotency record expires along with the order.
//                        .expiresAt(order.getExpiresAt())
//                        .build();
//
//        idempotencyRecordRepository.save(record);
//    }
//    private String generateOrderNumber() {
//
//        return "ORD-" + UUID.randomUUID();
//    }
//    private String generateRequestHash(CheckoutRequest request) {
//
//        // Build a deterministic representation of the
//        // business-relevant checkout data.
//        //
//        // orderId is intentionally excluded because it represents
//        // retry information rather than the actual checkout payload.
//        String requestData =
//                request.addressId()
//                        + "|"
//                        + request.cartItemIds()
//                        .stream()
//                        .sorted()
//                        .map(String::valueOf)
//                        .collect(Collectors.joining(","));
//
//        try {
//
//            // Generate SHA-256 hash for the request data.
//            MessageDigest digest =
//                    MessageDigest.getInstance("SHA-256");
//
//            byte[] hash =
//                    digest.digest(
//                            requestData.getBytes(StandardCharsets.UTF_8)
//                    );
//
//            // Convert the byte array into a readable hexadecimal string.
//            StringBuilder hexString =
//                    new StringBuilder();
//
//            for (byte b : hash) {
//                hexString.append(
//                        String.format("%02x", b)
//                );
//            }
//
//            return hexString.toString();
//
//        } catch (NoSuchAlgorithmException e) {
//
//            // SHA-256 is guaranteed by the Java platform,
//            // so this represents an unexpected configuration/runtime issue.
//            throw new IllegalStateException(
//                    "SHA-256 algorithm not available",
//                    e
//            );
//        }
//    }
//
//    private void updateIdempotencyRecord(Order order) {
//
//        IdempotencyRecord record =
//                idempotencyRecordRepository
//                        .findByOrderId(order.getId())
//                        .orElseThrow(() ->
//                                new RuntimeException(
//                                        "Idempotency record not found for order: "
//                                                + order.getOrderNumber()
//                                )
//                        );
//
//        record.setStatus(IdempotencyStatus.PROCESSING);
//        record.setExpiresAt(order.getExpiresAt());
//
//        idempotencyRecordRepository.save(record);
//    }
//    private Payment createPayment(
//            Order order,
//            BigDecimal amount
//    ) {
//
//        Payment payment =
//                Payment.builder()
//                        .order(order)
//                        .provider("PAYMENT_SIMULATOR")
//                        .providerPaymentId(
//                                "SIM-" + UUID.randomUUID()
//                        )
//                        .transactionRef(
//                                "TXN-" + UUID.randomUUID()
//                        )
//                        .paymentType("ONLINE")
//                        .status(PaymentStatus.PENDING)
//                        .amount(amount)
//                        .build();
//
//        return paymentRepository.save(payment);
//    }
//
//}
