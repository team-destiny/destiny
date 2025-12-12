package com.destiny.productservice.presentation.dto.request;

import java.util.UUID;

public record CreateProductRequest(
    String name,
    Integer price,
    UUID brandId,
    String color,
    String size,
    Integer quantity
) { }
