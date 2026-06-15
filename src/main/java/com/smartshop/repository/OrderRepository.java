package com.smartshop.repository;

import com.smartshop.entity.Order;
import com.smartshop.entity.User;
import com.smartshop.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // User ki saari orders — order history
    List<Order> findByUserOrderByCreatedAtDesc(User user);

    // Status se orders dhundo — Admin ke liye
    List<Order> findByStatus(OrderStatus status);

    // User ki specific status ki orders
    List<Order> findByUserAndStatus(User user, OrderStatus status);
}