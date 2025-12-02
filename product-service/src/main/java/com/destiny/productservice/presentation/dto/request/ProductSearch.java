package com.destiny.productservice.presentation.dto.request;

public record ProductSearch(
    Long minPrice,
    Long maxPrice,
    String nameContains,
    String brand,
    String size,
    String color
) { }
