package com.smartshop.service;

import com.smartshop.dto.ProductDto;
import java.util.List;

public interface ProductService {

    // Get all products
    List<ProductDto> getAllProducts();

    // Get product by id
    ProductDto getProductById(Long id);

    // Add new product — Admin only
    ProductDto addProduct(ProductDto productDto);

    // Update product — Admin only
    ProductDto updateProduct(Long id, ProductDto productDto);

    // Delete product — Admin only
    void deleteProduct(Long id);

    // Search products by keyword
    List<ProductDto> searchProducts(String keyword);

    // Get products by category
    List<ProductDto> getProductsByCategory(Long categoryId);
}