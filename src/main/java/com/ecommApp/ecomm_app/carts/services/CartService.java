package com.ecommApp.ecomm_app.carts.services;

import com.ecommApp.ecomm_app.carts.dto.CartItemRequest;
import com.ecommApp.ecomm_app.carts.dto.CartResponse;

public interface CartService {

    CartResponse getCart();

    CartResponse addItem(CartItemRequest request);

    CartResponse updateItem(Long cartItemId, Integer quantity);

    void removeItem(Long cartItemId);

    void clearCart();
}