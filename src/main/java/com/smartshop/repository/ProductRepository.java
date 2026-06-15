package com.smartshop.repository;

import com.smartshop.entity.Product;
import com.smartshop.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategory(Category category);
    
    List<Product> findByNameContainingIgnoreCase(String name);
    
    List<Product> findByCategoryAndNameContainingIgnoreCase(Category category, String name);
    
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchByKeyword(String keyword);
    
    // 🔥 Add this method
    @Modifying
    @Transactional
    @Query("DELETE FROM Product p WHERE p.category.id = :categoryId")
    void deleteByCategoryId(@Param("categoryId") Long categoryId);
}