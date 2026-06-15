package com.smartshop.dto;

public class CartItemDto {

	private Long id;
	private Integer quantity;
	private ProductDto product;

	// Default constructor
	public CartItemDto() {
	}

	// Parameterized constructor
	public CartItemDto(Long id, Integer quantity, ProductDto product) {
		super();
		this.id = id;
		this.quantity = quantity;
		this.product = product;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public ProductDto getProduct() {
		return product;
	}

	public void setProduct(ProductDto product) {
		this.product = product;
	}

}