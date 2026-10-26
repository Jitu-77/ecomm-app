//package com.ecommApp.ecomm_app.order.controllers;
//
//import com.ecommApp.ecomm_app.order.dto.CheckoutRequest;
//import com.ecommApp.ecomm_app.order.dto.OrderResponse;
//import com.ecommApp.ecomm_app.order.services.OrderService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/orders")
//@RequiredArgsConstructor
//public class OrderController {
//
//    private final OrderService orderService;
//
//    /**
//     * Checkout selected cart items and create a new order.
//     *
//     * The service handles:
//     * - Cart validation
//     * - Cart item validation
//     * - Address ownership validation
//     * - Inventory locking and reservation
//     * - Order creation
//     * - Order item creation
//     * - Order address snapshot
//     * - Idempotency record creation
//     */
//    @PostMapping("/checkout")
//    public ResponseEntity<OrderResponse> checkout(
//            @Valid @RequestBody CheckoutRequest request
//    ) {
//
//        OrderResponse response =
//                orderService.checkout(request);
//
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body(response);
//    }
//
//    /**
//     * Get all orders belonging to the currently authenticated user.
//     */
//    @GetMapping
//    public ResponseEntity<List<OrderResponse>> getMyOrders() {
//
//        List<OrderResponse> orders =
//                orderService.getMyOrders();
//
//        return ResponseEntity.ok(orders);
//    }
//
//    /**
//     * Get a specific order belonging to the currently authenticated user.
//     */
//    @GetMapping("/{orderId}")
//    public ResponseEntity<OrderResponse> getOrderById(
//            @PathVariable Long orderId
//    ) {
//
//        OrderResponse response =
//                orderService.getOrderById(orderId);
//
//        return ResponseEntity.ok(response);
//    }
//}