package com.smartshop.mapper;
import java.time.LocalDateTime;


import org.springframework.stereotype.Component;

import com.smartshop.dto.ProductDto;
import com.smartshop.entity.Category;
import com.smartshop.entity.Product;

@Component
public class ProductMapper {

    public ProductDto toDto(Product product) {

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

    public Product toEntity(ProductDto dto, Category category) {

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
}