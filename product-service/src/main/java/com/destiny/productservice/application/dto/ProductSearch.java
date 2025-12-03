package com.destiny.productservice.application.dto;

import java.util.UUID;

public record ProductSearch(
    Long minPrice,
    Long maxPrice,
    String nameContains,
    UUID brandId,
    String size,
    String color
) {

    public static ProductSearch of(
        Long minPrice,
        Long maxPrice,
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
