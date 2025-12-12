package com.destiny.stockservice.application.dto;

import com.destiny.stockservice.domain.entity.Stock;
import java.util.UUID;

public record StockCreateMessage(
    UUID productId,
    Integer quantity
) {
    public Stock toEntity() {

        Integer stockQuantity = (this.quantity != null) ? this.quantity : 0;

        return new Stock(
            this.productId,
            stockQuantity
        );
    }
}
