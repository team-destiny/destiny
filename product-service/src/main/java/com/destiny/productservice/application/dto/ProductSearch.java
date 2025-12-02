package com.destiny.productservice.application.dto;

public record ProductSearch(
    Long minPrice,
    Long maxPrice,
    String nameContains,
    String brand,
    String size,
    String color
) { }
