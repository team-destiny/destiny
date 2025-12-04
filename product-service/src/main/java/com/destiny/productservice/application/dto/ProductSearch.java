package com.destiny.productservice.application.dto;

import java.util.UUID;

public record ProductSearch(
    Integer minPrice,
    Integer maxPrice,
    String nameContains,
    UUID brandId,
    String size,
    String color
) {

    public static ProductSearch of(
        Integer minPrice,
        Integer maxPrice,
        String nameContains,
        UUID brandId,
        String size,
        String color
    ) {
        return new ProductSearch(
            minPrice,
            maxPrice,
            nameContains,
            brandId,
            size,
            color
        );
    }
}
