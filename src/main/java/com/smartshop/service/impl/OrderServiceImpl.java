package com.smartshop.service.impl;

import com.smartshop.dto.OrderDto;
import com.smartshop.dto.OrderItemDto;
import com.smartshop.entity.CartItem;
import com.smartshop.entity.Order;
import com.smartshop.entity.OrderItem;
import com.smartshop.entity.OrderStatus;
import com.smartshop.entity.Product;
import com.smartshop.entity.User;
import com.smartshop.repository.CartItemRepository;
import com.smartshop.repository.OrderItemRepository;
import com.smartshop.repository.OrderRepository;
import com.smartshop.repository.ProductRepository;
import com.smartshop.repository.UserRepository;
import com.smartshop.service.CartService;
import com.smartshop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    // Constants
    private static final int FREE_DELIVERY_THRESHOLD = 499;
    private static final int DELIVERY_CHARGE = 49;

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartService cartService;

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));
    }

    private OrderDto convertToDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUsername(order.getUser().getUsername());

        List<OrderItemDto> itemDtos = order.getOrderItems()
                .stream()
                .map(item -> {
                    OrderItemDto itemDto = new OrderItemDto();
                    itemDto.setId(item.getId());
                    itemDto.setProductId(item.getProduct().getId());
                    itemDto.setProductName(item.getProduct().getName());
                    itemDto.setQuantity(item.getQuantity());
                    itemDto.setPrice(item.getPrice());
                    itemDto.setProductImage(item.getProduct().getImageUrl());
                    return itemDto;
                })
                .collect(Collectors.toList());

        dto.setOrderItems(itemDtos);
        return dto;
    }

    @Override
    @Transactional
    public OrderDto createOrder(String email) {
        User user = getUser(email);
        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty!");
        }

        // Calculate subtotal (GST already included in product price)
        BigDecimal subtotal = cartItems.stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Add delivery charge if applicable
        BigDecimal totalAmount = subtotal;
        if (subtotal.compareTo(BigDecimal.valueOf(FREE_DELIVERY_THRESHOLD)) < 0) {
            totalAmount = subtotal.add(BigDecimal.valueOf(DELIVERY_CHARGE));
        }

        Order order = new Order(user, totalAmount);
        Order savedOrder = orderRepository.save(order);

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            if (cartItem.getProduct().getStock() < cartItem.getQuantity()) {
                throw new RuntimeException(cartItem.getProduct().getName() + " is out of stock!");
            }
            
            Product product = cartItem.getProduct();
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem(
                    savedOrder,
                    cartItem.getProduct(),
                    cartItem.getQuantity(),
                    cartItem.getProduct().getPrice()
            );
            orderItems.add(orderItem);
        }
        orderItemRepository.saveAll(orderItems);
        cartService.clearCart(email);
        
        return convertToDto(savedOrder);
    }

    @Override
    @Transactional
    public OrderDto createOrderFromPayment(String email) {
        User user = getUser(email);
        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty!");
        }

        // Calculate subtotal (GST already included in product price)
        BigDecimal subtotal = cartItems.stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Add delivery charge if applicable (NO GST ADDITION!)
        BigDecimal totalAmount = subtotal;
        if (subtotal.compareTo(BigDecimal.valueOf(FREE_DELIVERY_THRESHOLD)) < 0) {
            totalAmount = subtotal.add(BigDecimal.valueOf(DELIVERY_CHARGE));
        }

        // Create order
        Order order = new Order(user, totalAmount);
        Order savedOrder = orderRepository.save(order);

        // Create order items and update stock
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            if (cartItem.getProduct().getStock() < cartItem.getQuantity()) {
                throw new RuntimeException(cartItem.getProduct().getName() + " is out of stock!");
            }
            
            Product product = cartItem.getProduct();
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem(
                    savedOrder,
                    cartItem.getProduct(),
                    cartItem.getQuantity(),
                    cartItem.getProduct().getPrice()
            );
            orderItems.add(orderItem);
        }
        orderItemRepository.saveAll(orderItems);
        cartService.clearCart(email);
        
        return convertToDto(savedOrder);
    }

    @Override
    @Transactional
    public OrderDto createOrderAfterPayment(String email) {
        return createOrderFromPayment(email);
    }

    @Override
    public OrderDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found!"));
        return convertToDto(order);
    }

    @Override
    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDto updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found!"));
        order.setStatus(status);
        Order updated = orderRepository.save(order);
        return convertToDto(updated);
    }
    
    @Override
    public List<OrderDto> getUserOrders(String email) {
        User user = getUser(email);
        return orderRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}