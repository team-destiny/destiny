package com.destiny.productservice.presentation.dto.request;

import com.destiny.productservice.domain.entity.ProductStatus;
import java.util.UUID;

public record UpdateProductRequest(
    String name,
    Integer price,
    UUID brandId,
    ProductStatus status,
    String color,
    String size
) { }
