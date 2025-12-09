package com.destiny.productservice.presentation.dto.request;

public record CreateProductRequest(
    String name,
    Integer price,
    String color,
    String size,
    Integer quantity
) { }
