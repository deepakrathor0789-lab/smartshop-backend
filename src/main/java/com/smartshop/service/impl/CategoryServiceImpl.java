package com.smartshop.service.impl;

import com.smartshop.entity.Category;
import com.smartshop.entity.Product;
import com.smartshop.repository.*;
import com.smartshop.service.CategoryService;

import java.util.List;
import java.util.Optional;  // 🔥 ADD THIS IMPORT

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        // Check if category exists
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        
        // Get all products in this category
        List<Product> products = productRepository.findByCategory(category);
        
        for (Product product : products) {
            // Delete order items for this product
            orderItemRepository.deleteByProductId(product.getId());
            // Delete cart items for this product
            cartItemRepository.deleteByProductId(product.getId());
        }
        
        // Delete all products in this category
        productRepository.deleteByCategoryId(id);
        
        // Finally delete category
        categoryRepository.delete(category);
    }
    
    @Override
    @Transactional
    public Category addCategory(Category category) {
        // Check if category with same name exists (even inactive)
        Optional<Category> existing = categoryRepository.findByName(category.getName());
        
        if (existing.isPresent()) {
            Category existingCat = existing.get();
            if (!existingCat.isActive()) {
                // Reactivate existing category instead of creating new
                existingCat.setActive(true);
                existingCat.setDescription(category.getDescription());
                return categoryRepository.save(existingCat);
            } else {
                throw new RuntimeException("Category with name '" + category.getName() + "' already exists!");
            }
        }
        
        category.setActive(true);
        return categoryRepository.save(category);
    }
}