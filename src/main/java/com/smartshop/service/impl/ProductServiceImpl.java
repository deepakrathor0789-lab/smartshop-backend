package com.smartshop.service.impl;

import com.smartshop.dto.ProductDto;
import com.smartshop.entity.Category;
import com.smartshop.entity.Product;
import com.smartshop.repository.CartItemRepository;
import com.smartshop.repository.CategoryRepository;
import com.smartshop.repository.OrderItemRepository;
import com.smartshop.repository.ProductRepository;
import com.smartshop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    // Convert Product entity to ProductDto
    private ProductDto convertToDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setImageUrl(product.getImageUrl());
        dto.setCategoryId(product.getCategory().getId());
        dto.setCategoryName(product.getCategory().getName());
        return dto;
    }

    // Convert ProductDto to Product entity
    private Product convertToEntity(ProductDto dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found!"));

        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setImageUrl(dto.getImageUrl());
        product.setCategory(category);
        product.setCreatedAt(LocalDateTime.now());
        return product;
    }

    @Override
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found!"));
        return convertToDto(product);
    }

    @Override
    public ProductDto addProduct(ProductDto productDto) {
        Product product = convertToEntity(productDto);
        Product saved = productRepository.save(product);
        return convertToDto(saved);
    }

    @Override
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found!"));

        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setStock(productDto.getStock());
        product.setImageUrl(productDto.getImageUrl());

        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found!"));
        product.setCategory(category);

        Product updated = productRepository.save(product);
        return convertToDto(updated);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        // Check if product exists
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        // Delete from order_items first (foreign key constraint)
        orderItemRepository.deleteByProductId(id);
        
        // Delete from cart_items (foreign key constraint)
        cartItemRepository.deleteByProductId(id);
        
        // Finally delete the product
        productRepository.delete(product);
    }

    @Override
    public List<ProductDto> searchProducts(String keyword) {
        return productRepository.searchByKeyword(keyword)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> getProductsByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found!"));
        return productRepository.findByCategory(category)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}