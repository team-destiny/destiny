package com.destiny.productservice.application.dto;

import com.destiny.productservice.domain.entity.Product;
import java.util.UUID;

public record ProductValidationMessage(
    UUID productId,
    UUID brandId,
    Integer price
) {
    public static ProductValidationMessage from(Product product) {
        return new ProductValidationMessage(
            product.getId(),
            product.getBrandId(),
            product.getPrice()
        );
    }
}
