package com.destiny.productservice.presentation.dto.request;

public record CreateProductRequest(
    String name,
    Long price,
    String brand,
    String color,
    String size
) { }
