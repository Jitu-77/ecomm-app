package com.ecommApp.ecomm_app.carts.services;

import com.ecommApp.ecomm_app.carts.dto.CartItemRequest;
import com.ecommApp.ecomm_app.carts.dto.CartItemResponse;
import com.ecommApp.ecomm_app.carts.dto.CartResponse;
import com.ecommApp.ecomm_app.carts.entity.Cart;
import com.ecommApp.ecomm_app.carts.entity.CartItem;
import com.ecommApp.ecomm_app.carts.mapper.CartItemMapper;
import com.ecommApp.ecomm_app.carts.mapper.CartMapper;
import com.ecommApp.ecomm_app.carts.repository.CartRepository;
import com.ecommApp.ecomm_app.carts.repository.CartItemRepository;
import com.ecommApp.ecomm_app.common.enums.CartStatus;
import com.ecommApp.ecomm_app.common.exceptions.InsufficientQuantityException;
import com.ecommApp.ecomm_app.common.exceptions.ResourceNotFoundException;
import com.ecommApp.ecomm_app.inventory.entity.Inventory;
import com.ecommApp.ecomm_app.inventory.repository.InventoryRepository;
import com.ecommApp.ecomm_app.products.entity.Product;
import com.ecommApp.ecomm_app.products.repository.ProductRepository;
import com.ecommApp.ecomm_app.user.auth.SecurityUtils;
import com.ecommApp.ecomm_app.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;

    private final InventoryRepository inventoryRepository;

    @Override
//    @Transactional(readOnly = true)
    @Transactional
    public CartResponse getCart() {

        User currentUser = SecurityUtils.getCurrentUser();

        Cart cart = cartRepository.findByUser(currentUser)
                .orElseGet(() -> createCart(currentUser));

        return buildCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse addItem(CartItemRequest request) {

        User currentUser = SecurityUtils.getCurrentUser();

        Cart cart = cartRepository.findByUser(currentUser)
                .orElseGet(() -> createCart(currentUser));

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: " + request.productId()
                        )
                );

        if (!product.getActive()) {
            throw new IllegalStateException(
                    "Product is not active"
            );
        }

        CartItem cartItem = cartItemRepository
                .findByCartAndProduct(cart, product)
                .orElse(null);

        Inventory inventory = inventoryRepository
                .findByProductId(product.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Inventory not found for product: "
                                        + product.getId()
                        )
                );

        validateAddToCart(
                product,
                cartItem,
                inventory
        );

        if (cartItem != null) {

            cartItem.setQuantity(
                    cartItem.getQuantity() + 1
            );

        } else {

            cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(1)
                    .unitPrice(product.getPrice())
                    .build();

            cart.getItems().add(cartItem);
        }

        cartItemRepository.save(cartItem);

        return buildCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse updateItem(Long cartItemId, Integer newQuantity) {

        User currentUser = SecurityUtils.getCurrentUser();

        Cart cart = cartRepository.findByUser(currentUser)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cart not found")
                );

        CartItem cartItem = cartItemRepository
                .findByIdAndCart(cartItemId, cart)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cart item not found with id: " + cartItemId
                        )
                );

        Product product = cartItem.getProduct();

        Inventory inventory = inventoryRepository
                .findByProductId(product.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Inventory not found for product: "
                                        + product.getId()
                        )
                );

        validateUpdateQuantity(
                product,
                cartItem,
                inventory,
                newQuantity
        );

        cartItem.setQuantity(newQuantity);

        cartItemRepository.save(cartItem);

        return buildCartResponse(cart);
    }



    @Override
    @Transactional
    public void removeItem(Long cartItemId) {

        User currentUser = SecurityUtils.getCurrentUser();

        Cart cart = cartRepository.findByUser(currentUser)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cart not found")
                );

        CartItem cartItem = cartItemRepository
                .findByIdAndCart(cartItemId, cart)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cart item not found with id: " + cartItemId
                        )
                );

        cart.getItems().remove(cartItem);
        cartItemRepository.delete(cartItem);
    }

    @Override
    @Transactional
    public void clearCart() {

        User currentUser = SecurityUtils.getCurrentUser();

        Cart cart = cartRepository.findByUser(currentUser)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cart not found")
                );

        cart.getItems().clear();
    }

    private Cart createCart(User user) {

        Cart cart = Cart.builder()
                .user(user)
                .status(CartStatus.ACTIVE)
                .build();

        return cartRepository.save(cart);
    }

    private CartResponse buildCartResponse(Cart cart) {

        List<CartItemResponse> itemResponses = cart.getItems()
                .stream()
                .map(cartItem -> {
                    CartItemResponse response =
                            cartItemMapper.toResponse(cartItem);

                    BigDecimal subtotal =
                            cartItem.getUnitPrice()
                                    .multiply(
                                            BigDecimal.valueOf(
                                                    cartItem.getQuantity()
                                            )
                                    );

                    return new CartItemResponse(
                            response.id(),
                            response.productId(),
                            response.productName(),
                            response.unitPrice(),
                            response.quantity(),
                            subtotal
                    );
                })
                .toList();

        BigDecimal total = itemResponses.stream()
                .map(CartItemResponse::subtotal)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );

        CartResponse response = cartMapper.toResponse(cart);

        return new CartResponse(
                response.id(),
                response.status(),
                itemResponses,
                total
        );
    }

    private void validateQuantity(Integer quantity) {

        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }
    }

    private void validateAddToCart(
            Product product,
            CartItem cartItem,
            Inventory inventory
    ) {

        int currentQuantity = cartItem == null
                ? 0
                : cartItem.getQuantity();

        int finalQuantity = currentQuantity + 1;

        if (finalQuantity > inventory.getAvailableQty()) {
            throw new IllegalStateException(
                    "Insufficient inventory for product: " + product.getName()
            );
        }
    }
    private void validateUpdateQuantity(
            Product product,
            CartItem cartItem,
            Inventory inventory,
            Integer newQuantity
    ) {

        if (newQuantity == null || newQuantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }

        int currentQuantity = cartItem.getQuantity();

        // Decrease → no inventory check
        if (newQuantity < currentQuantity) {
            return;
        }

        // Increase → check inventory
        if (newQuantity > inventory.getAvailableQty()) {
            throw new InsufficientQuantityException(
                    "Insufficient inventory for product: " + product.getName()
            );
        }
    }
}