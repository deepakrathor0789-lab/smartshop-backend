package com.smartshop.controller;

import com.smartshop.dto.OrderDto;
import com.smartshop.entity.OrderStatus;
import com.smartshop.payment.RazorpayService;
import com.smartshop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private RazorpayService razorpayService;

    // Get logged in user email
    private String getEmail() {
        Authentication authentication = SecurityContextHolder
                .getContext().getAuthentication();
        return authentication.getName();
    }

    // Create order from cart — User
    @PostMapping("/create")
    public ResponseEntity<OrderDto> createOrder() {
        return ResponseEntity.ok(orderService.createOrder(getEmail()));
    }

    // 🔥 NEW: Create order AFTER payment success — with stock update
    @PostMapping("/create-after-payment")
    public ResponseEntity<OrderDto> createOrderAfterPayment(
            @RequestBody Map<String, String> paymentData) {
        
        String razorpayOrderId = paymentData.get("razorpayOrderId");
        String razorpayPaymentId = paymentData.get("razorpayPaymentId");
        String razorpaySignature = paymentData.get("razorpaySignature");
        
        // First verify payment signature
        boolean isValid = razorpayService.verifyPayment(
                razorpayOrderId, razorpayPaymentId, razorpaySignature);
        
        if (!isValid) {
            throw new RuntimeException("Payment verification failed!");
        }
        
        // Only after verification — create order and update stock
        String email = getEmail();
        OrderDto order = orderService.createOrderAfterPayment(email);
        
        // Update payment record
        razorpayService.updatePaymentStatus(
                razorpayOrderId, razorpayPaymentId, razorpaySignature, true);
        
        return ResponseEntity.ok(order);
    }

    // 🔥 NEW: Create order from payment (for checkout flow)
    @PostMapping("/create-from-payment")
    public ResponseEntity<OrderDto> createOrderFromPayment(
            @RequestBody Map<String, String> paymentData) {
        
        String razorpayOrderId = paymentData.get("razorpayOrderId");
        String razorpayPaymentId = paymentData.get("razorpayPaymentId");
        String razorpaySignature = paymentData.get("razorpaySignature");
        
        System.out.println("=== Creating order from payment ===");
        System.out.println("Razorpay Order ID: " + razorpayOrderId);
        
        // Verify payment signature
        boolean isValid = razorpayService.verifyPayment(
                razorpayOrderId, razorpayPaymentId, razorpaySignature);
        
        if (!isValid) {
            throw new RuntimeException("Payment verification failed!");
        }
        
        // Create order and update stock
        String email = getEmail();
        OrderDto order = orderService.createOrderFromPayment(email);
        
        // Update payment record
        razorpayService.updatePaymentStatus(
                razorpayOrderId, razorpayPaymentId, razorpaySignature, true);
        
        System.out.println("Order created successfully! ID: " + order.getId());
        
        return ResponseEntity.ok(order);
    }

    // Get order history — User
    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderDto>> getUserOrders() {
        String email = getEmail();
        return ResponseEntity.ok(orderService.getUserOrders(email));
    }
    
    // Get order by id — User
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    // Get all orders — Admin only
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // Update order status — Admin only
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDto> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }
}