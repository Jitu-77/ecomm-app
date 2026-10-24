package com.ecommApp.ecomm_app.carts.controllers;

import com.ecommApp.ecomm_app.carts.dto.CartItemRequest;
import com.ecommApp.ecomm_app.carts.dto.CartResponse;

import com.ecommApp.ecomm_app.carts.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart() {

        return ResponseEntity.ok(
                cartService.getCart()
        );
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(
            @RequestBody CartItemRequest request
    ) {

        return ResponseEntity.ok(
                cartService.addItem(request)
        );
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> updateItem(
            @PathVariable Long cartItemId,
            @RequestBody Integer quantity
    ) {

        return ResponseEntity.ok(
                cartService.updateItem(cartItemId, quantity)
        );
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Void> removeItem(
            @PathVariable Long cartItemId
    ) {

        cartService.removeItem(cartItemId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/items")
    public ResponseEntity<Void> clearCart() {

        cartService.clearCart();

        return ResponseEntity.noContent().build();
    }
}
