package com.smartshop.payment;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.json.JSONObject;
import com.smartshop.entity.CartItem;
import com.smartshop.mapper.CartItemMapper;
import com.smartshop.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smartshop.entity.Payment;
import com.smartshop.entity.PaymentStatus;
import com.smartshop.entity.User;

import com.smartshop.repository.OrderRepository;
import com.smartshop.repository.PaymentRepository;
import com.smartshop.repository.UserRepository;

import java.math.BigDecimal;
import com.razorpay.RazorpayClient;
import com.smartshop.dto.CartItemDto;
import com.smartshop.dto.OrderDto;
import com.smartshop.service.CartService;
import com.smartshop.service.OrderService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "http://localhost:5173")
public class RazorpayController {

    @Autowired
    private CartItemMapper cartItemMapper;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RazorpayService razorpayService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartService cartService;

    @Value("${razorpay.key.id}")
    private String keyId;

    @Autowired
    private RazorpayClient razorpayClient;

    private String getEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    // Create Razorpay order (existing - with orderId)
    @PostMapping("/create/{orderId}")
    public ResponseEntity<Map<String, Object>> createPayment(@PathVariable Long orderId) {
        try {
            Map<String, Object> response = razorpayService.createRazorpayOrder(orderId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Payment creation failed: " + e.getMessage());
        }
    }

    // Verify payment after success
    @PostMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyPayment(@RequestBody Map<String, String> paymentData) {
        String razorpayOrderId = paymentData.get("razorpayOrderId");
        String razorpayPaymentId = paymentData.get("razorpayPaymentId");
        String razorpaySignature = paymentData.get("razorpaySignature");

        boolean isValid = razorpayService.verifyPayment(razorpayOrderId, razorpayPaymentId, razorpaySignature);
        razorpayService.updatePaymentStatus(razorpayOrderId, razorpayPaymentId, razorpaySignature, isValid);

        if (isValid) {
            return ResponseEntity.ok(Map.of("message", "Payment successful!", "status", "SUCCESS"));
        } else {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Payment verification failed!", "status", "FAILED"));
        }
    }

    // 🔥 MAIN CREATE ORDER ENDPOINT (with stock check)
    @PostMapping("/create-order")
    public ResponseEntity<Map<String, Object>> createRazorpayOrder(@RequestBody Map<String, Object> request) {
        try {
            int amountInPaise = ((Number) request.get("amount")).intValue() * 100;

            // Stock check before creating order
            String email = getEmail();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found!"));

            List<CartItem> cartItems = cartItemRepository.findByUser(user);

            for (CartItem cartItem : cartItems) {
                if (cartItem.getProduct().getStock() < cartItem.getQuantity()) {
                    throw new RuntimeException(cartItem.getProduct().getName() 
                        + " has insufficient stock! Available: " 
                        + cartItem.getProduct().getStock() 
                        + ", Requested: " + cartItem.getQuantity());
                }
            }

            // Create Razorpay order
            JSONObject options = new JSONObject();
            options.put("amount", amountInPaise);
            options.put("currency", "INR");
            options.put("receipt", "order_" + System.currentTimeMillis());

            com.razorpay.Order razorpayOrder = razorpayClient.orders.create(options);
            String razorpayOrderId = razorpayOrder.get("id");

            // Save payment record
            Payment payment = new Payment();
            payment.setRazorpayOrderId(razorpayOrderId);
            payment.setAmount(BigDecimal.valueOf(amountInPaise / 100.0));
            payment.setStatus(PaymentStatus.PENDING);
            paymentRepository.save(payment);

            Map<String, Object> response = new HashMap<>();
            response.put("razorpayOrderId", razorpayOrderId);
            response.put("amount", amountInPaise);
            response.put("currency", "INR");
            response.put("keyId", keyId);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Payment creation failed: " + e.getMessage());
        }
    }

    // Create order after payment success
    @PostMapping("/create-from-payment")
    public ResponseEntity<OrderDto> createOrderFromPayment(@RequestBody Map<String, String> paymentData) {
        String razorpayOrderId = paymentData.get("razorpayOrderId");
        String razorpayPaymentId = paymentData.get("razorpayPaymentId");
        String razorpaySignature = paymentData.get("razorpaySignature");

        boolean isValid = razorpayService.verifyPayment(razorpayOrderId, razorpayPaymentId, razorpaySignature);

        if (!isValid) {
            throw new RuntimeException("Payment verification failed!");
        }

        String email = getEmail();
        OrderDto order = orderService.createOrderFromPayment(email);

        razorpayService.updatePaymentStatus(razorpayOrderId, razorpayPaymentId, razorpaySignature, true);

        return ResponseEntity.ok(order);
    }

    // Update cart item quantity
    @PutMapping("/update/{cartItemId}")
    public ResponseEntity<CartItemDto> updateQuantity(@PathVariable Long cartItemId, @RequestParam Integer quantity) {
        String email = getEmail();
        CartItem cartItem = cartService.updateQuantity(cartItemId, quantity, email);
        return ResponseEntity.ok(cartItemMapper.toDto(cartItem));
    }
}