package com.smartshop.payment;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.smartshop.entity.Order;
import com.smartshop.entity.Payment;
import com.smartshop.entity.PaymentStatus;
import com.smartshop.repository.OrderRepository;
import com.smartshop.repository.PaymentRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class RazorpayService {

    @Autowired
    private RazorpayClient razorpayClient;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    // Create Razorpay order
    public Map<String, Object> createRazorpayOrder(Long orderId) 
            throws RazorpayException {

        // Get order from database
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found!"));

        // Amount in paise (1 rupee = 100 paise)
        int amountInPaise = order.getTotalAmount()
                .multiply(BigDecimal.valueOf(100))
                .intValue();

        // Create Razorpay order options
        JSONObject options = new JSONObject();
        options.put("amount", amountInPaise);
        options.put("currency", "INR");
        options.put("receipt", "order_" + orderId);

        // Create order on Razorpay
        com.razorpay.Order razorpayOrder = razorpayClient.orders.create(options);

        // Save payment record in database
        Payment payment = new Payment(order, order.getTotalAmount());
        payment.setRazorpayOrderId(razorpayOrder.get("id"));
        paymentRepository.save(payment);

        // Return response to frontend
        Map<String, Object> response = new HashMap<>();
        response.put("razorpayOrderId", razorpayOrder.get("id"));
        response.put("amount", amountInPaise);
        response.put("currency", "INR");
        response.put("keyId", keyId);
        return response;
    }

    // Verify payment signature
    public boolean verifyPayment(String razorpayOrderId,
                                  String razorpayPaymentId,
                                  String razorpaySignature) {
        try {
            // Create signature string
            String data = razorpayOrderId + "|" + razorpayPaymentId;

            // Generate HMAC SHA256 signature
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(
                    keySecret.getBytes(), "HmacSHA256");
            mac.init(secretKey);
            byte[] hash = mac.doFinal(data.getBytes());

            // Convert to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            // Compare signatures
            return hexString.toString().equals(razorpaySignature);

        } catch (Exception e) {
            return false;
        }
    }

    // Update payment status after verification
    public void updatePaymentStatus(String razorpayOrderId,
                                     String razorpayPaymentId,
                                     String razorpaySignature,
                                     boolean isSuccess) {

        Payment payment = paymentRepository
                .findByRazorpayOrderId(razorpayOrderId)
                .orElseThrow(() -> new RuntimeException("Payment not found!"));

        payment.setRazorpayPaymentId(razorpayPaymentId);
        payment.setRazorpaySignature(razorpaySignature);

        if (isSuccess) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setPaidAt(LocalDateTime.now());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }

        paymentRepository.save(payment);
    }
}