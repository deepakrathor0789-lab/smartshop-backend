package com.smartshop.service;

import com.smartshop.entity.Category;

public interface CategoryService {
    
    void deleteCategory(Long id);
    
    Category addCategory(Category category);  // 🔥 Add this method
}