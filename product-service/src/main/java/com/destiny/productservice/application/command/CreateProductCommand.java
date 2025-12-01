package com.destiny.productservice.application.command;

import com.destiny.productservice.domain.entity.Product;
import com.destiny.productservice.domain.entity.ProductStatus;

public record CreateProductCommand(
    String name,
    Long price,
    String brand,
    String color,
    String size
) {
    public Product toEntity() {
        return new Product(
            null,
            name,
            price,
            brand,
            ProductStatus.AVAILABLE,
            color,
            size
        );
    }
}
