package com.destiny.productservice.presentation.dto.request;

import com.destiny.productservice.domain.entity.ProductStatus;

public record UpdateProductRequest(
    String name,
    Integer price,
    ProductStatus status,
    String color,
    String size
) { }
