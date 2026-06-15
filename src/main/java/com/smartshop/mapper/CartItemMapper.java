package com.smartshop.mapper;

import org.springframework.stereotype.Component;

import com.smartshop.dto.CartItemDto;
import com.smartshop.entity.CartItem;

@Component
public class CartItemMapper {

    private final ProductMapper productMapper;

    public CartItemMapper(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    public CartItemDto toDto(CartItem cartItem) {

        CartItemDto dto = new CartItemDto();
        dto.setId(cartItem.getId());
        dto.setQuantity(cartItem.getQuantity());

        if (cartItem.getProduct() != null) {
            dto.setProduct(productMapper.toDto(cartItem.getProduct()));
        }

        return dto;
    }
}