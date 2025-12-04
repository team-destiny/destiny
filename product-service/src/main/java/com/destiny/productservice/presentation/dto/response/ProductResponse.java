package com.destiny.productservice.presentation.dto.response;

import com.destiny.productservice.domain.entity.Product;
import com.destiny.productservice.domain.entity.ProductStatus;
import com.destiny.productservice.domain.entity.ProductView;
import java.util.UUID;

public record ProductResponse(
    UUID id,
    String name,
    Integer price,
    UUID brandId,
    ProductStatus status,
    String color,
    String size
) {
    public static ProductResponse of(Product product) {
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getPrice(),
            product.getBrandId(),
            product.getStatus(),
            product.getColor(),
            product.getSize()
        );
    }

    public static ProductResponse of(ProductView productView) {
        return new ProductResponse(
            productView.getId(),
            productView.getName(),
            productView.getPrice(),
            productView.getBrandId(),
            productView.getStatus(),
            productView.getColor(),
            productView.getSize()
        );
    }
}
