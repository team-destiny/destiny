package com.destiny.productservice.application.dto;

import com.destiny.productservice.domain.entity.Product;
import com.destiny.productservice.domain.entity.ProductStatus;
import java.util.UUID;

public record ProductMessage (
    UUID id,
    String name,
    Long price,
    String brand,
    ProductStatus status,
    String color,
    String size
) {
    public static ProductMessage from(Product product) {
        return new ProductMessage(
            product.getId(),
            product.getName(),
            product.getPrice(),
            product.getBrand(),
            product.getStatus(),
            product.getColor(),
            product.getSize()
        );
    }
}
