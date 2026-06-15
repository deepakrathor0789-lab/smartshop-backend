package com.smartshop.service;

import com.smartshop.entity.CartItem;
import java.util.List;

public interface CartService {

    // Add product to cart
    CartItem addToCart(Long productId, Integer quantity, String email);

    // Remove product from cart
    void removeFromCart(Long cartItemId, String email);

    // Get all cart items of user
    List<CartItem> getCartItems(String email);

    // Clear cart after order placed
    void clearCart(String email);
    CartItem updateQuantity(Long cartItemId, Integer quantity, String email);
}