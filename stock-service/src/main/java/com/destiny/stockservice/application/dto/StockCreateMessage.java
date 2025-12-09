package com.destiny.stockservice.application.dto;

import com.destiny.stockservice.domain.entity.Stock;
import java.util.UUID;

public record StockCreateMessage(
    UUID productId,
    Integer quantity
) {
    public Stock toEntity() {

        Integer stockQuantity = 0;

        if (this.quantity != null) {
            stockQuantity = this.quantity;
        }

        return new Stock(
            this.productId,
            stockQuantity
        );
    }
}
