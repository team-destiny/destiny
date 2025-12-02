package com.destiny.productservice.presentation.dto.request;

import com.destiny.productservice.domain.entity.ProductStatus;
import java.util.UUID;

public record UpdateProductRequest(
    UUID id,
    String name,
    Long price,
    String brand,
    ProductStatus status,
    String color,
    String size
) { }
