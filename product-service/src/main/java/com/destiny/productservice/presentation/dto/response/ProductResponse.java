package com.destiny.productservice.presentation.dto.response;

import com.destiny.productservice.domain.entity.Product;
import com.destiny.productservice.domain.entity.ProductStatus;
import java.util.UUID;

public record ProductResponse(
    UUID id,
    String name,
    Long price,
    String brand,
    ProductStatus status,
    String color,
    String size
) {
    public static ProductResponse of(Product product) {
        return new ProductResponse(
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
