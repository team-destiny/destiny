package com.destiny.productservice.application.dto;

import java.util.UUID;

public record ProductSearch(
    Long minPrice,
    Long maxPrice,
    String nameContains,
    UUID brandId,
    String size,
    String color
) { }
