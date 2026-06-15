package com.smartshop.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smartshop.dto.CartItemDto;
import com.smartshop.entity.CartItem;
import com.smartshop.mapper.CartItemMapper;
import com.smartshop.service.CartService;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "http://localhost:5173")
public class CartController {

	@Autowired

	private CartService cartService;

	@Autowired
	private CartItemMapper cartItemMapper;

	private String getEmail() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication.getName();
	}

	@PostMapping("/add")
	public ResponseEntity<CartItemDto> addToCart(@RequestParam Long productId, @RequestParam Integer quantity) {

		CartItem cartItem = cartService.addToCart(productId, quantity, getEmail());

		return ResponseEntity.ok(cartItemMapper.toDto(cartItem));
	}
	@GetMapping
	public ResponseEntity<List<CartItemDto>> getCart() {

	    List<CartItem> cartItems = cartService.getCartItems(getEmail());

	    return ResponseEntity.ok(
	            cartItems.stream()
	                    .map(cartItemMapper::toDto)
	                    .toList()
	    );
	}
	@DeleteMapping("/remove/{cartItemId}")
	public ResponseEntity<String> removeFromCart(@PathVariable Long cartItemId) {
		cartService.removeFromCart(cartItemId, getEmail());
		return ResponseEntity.ok("Item removed from cart!");
	}

	@DeleteMapping("/clear")
	public ResponseEntity<String> clearCart() {
		cartService.clearCart(getEmail());
		return ResponseEntity.ok("Cart cleared!");
	}
}