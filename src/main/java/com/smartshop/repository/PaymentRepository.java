package com.smartshop.repository;

import com.smartshop.entity.Payment;
import com.smartshop.entity.Order;
import com.smartshop.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Find payment by order
    Optional<Payment> findByOrder(Order order);

    // Find payment by razorpay order id
    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);

    // Find all payments by status
    List<Payment> findByStatus(PaymentStatus status);
}