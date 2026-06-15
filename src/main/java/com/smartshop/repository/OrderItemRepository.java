package com.smartshop.repository;

import com.smartshop.entity.OrderItem;
import com.smartshop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;  // ← Change this import
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrder(Order order);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM OrderItem o WHERE o.product.id = :productId")
    int deleteByProductId(@Param("productId") Long productId);  // void → int
    List<OrderItem> findByProductId(Long productId);
}