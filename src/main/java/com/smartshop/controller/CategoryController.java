package com.smartshop.controller;

import com.smartshop.entity.Category;
import com.smartshop.repository.CategoryRepository;
import com.smartshop.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "http://localhost:5173")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryService categoryService;  // 🔥 Add this

    // Get all active categories — Public
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryRepository.findByActiveTrue());
    }

    // Add category — Admin only (using service)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Category> addCategory(@RequestBody Category category) {
        Category saved = categoryService.addCategory(category);  // 🔥 Use service method
        return ResponseEntity.ok(saved);
    }

    // Delete category — Admin only (using service)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Category deleted successfully!");
    }
}