package com.smartshop.service.impl;

import com.smartshop.entity.CartItem;
import com.smartshop.entity.Product;
import com.smartshop.entity.User;
import com.smartshop.repository.CartItemRepository;
import com.smartshop.repository.ProductRepository;
import com.smartshop.repository.UserRepository;
import com.smartshop.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    // Get logged in user from email
    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));
    }

    @Override
    public CartItem addToCart(Long productId, Integer quantity, String email) {
        User user = getUser(email);

        // Check if product exists
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found!"));

        // Check if product already in cart
        Optional<CartItem> existingItem = cartItemRepository
                .findByUserAndProduct(user, product);

        if (existingItem.isPresent()) {
            // Product already in cart — just update quantity
            CartItem cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            return cartItemRepository.save(cartItem);
        }

        // Product not in cart — add new cart item
        CartItem cartItem = new CartItem(user, product, quantity);
        return cartItemRepository.save(cartItem);
    }
    
    @Override
    public CartItem updateQuantity(Long cartItemId, Integer quantity, String email) {
        User user = getUser(email);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found!"));
        
        // Security check
        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access!");
        }
        
        // Stock check
        if (cartItem.getProduct().getStock() < quantity) {
            throw new RuntimeException("Insufficient stock!");
        }
        
        cartItem.setQuantity(quantity);
        return cartItemRepository.save(cartItem);
    }

    @Override
    public void removeFromCart(Long cartItemId, String email) {
        User user = getUser(email);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found!"));

        // Make sure user can only remove their own cart items
        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access!");
        }

        cartItemRepository.delete(cartItem);
    }

    @Override
    public List<CartItem> getCartItems(String email) {
        User user = getUser(email);
        return cartItemRepository.findByUser(user);
    }

    @Override
    public void clearCart(String email) {
        User user = getUser(email);
        cartItemRepository.deleteByUser(user);
    }
}