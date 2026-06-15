package com.smartshop.repository;

import com.smartshop.entity.CartItem;
import com.smartshop.entity.User;

import jakarta.transaction.Transactional;

import com.smartshop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

	@Query("SELECT c FROM CartItem c JOIN FETCH c.product WHERE c.user = :user")
	List<CartItem> findByUser(@Param("user") User user);

    // User ka specific product cart mein hai ya nahi
    Optional<CartItem> findByUserAndProduct(User user, Product product);

    // User ke saare cart items delete karo (order place hone ke baad)
    void deleteByUser(User user);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem c WHERE c.product.id = :productId")
    int deleteByProductId(@Param("productId") Long productId);  // void → int
}