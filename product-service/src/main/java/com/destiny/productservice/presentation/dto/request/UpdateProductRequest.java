package com.destiny.productservice.presentation.dto.request;

import com.destiny.productservice.domain.entity.ProductStatus;

public record UpdateProductRequest(
    String name,
    Long price,
    String brand,
    ProductStatus status,
    String color,
    String size
) { }
