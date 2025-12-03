package com.destiny.productservice.presentation.dto.request;

import java.util.UUID;

public record CreateProductRequest(
    String name,
    Long price,
    UUID brandId,
    String color,
    String size
) { }
