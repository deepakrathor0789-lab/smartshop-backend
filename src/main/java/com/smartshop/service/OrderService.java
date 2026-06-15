package com.smartshop.service;

import com.smartshop.dto.OrderDto;
import com.smartshop.entity.OrderStatus;
import java.util.List;

public interface OrderService {

    OrderDto createOrder(String email);
    
    OrderDto createOrderFromPayment(String email);
    
    OrderDto createOrderAfterPayment(String email);

    List<OrderDto> getUserOrders(String email);

    OrderDto getOrderById(Long id);

    List<OrderDto> getAllOrders();

    OrderDto updateOrderStatus(Long id, OrderStatus status);
}