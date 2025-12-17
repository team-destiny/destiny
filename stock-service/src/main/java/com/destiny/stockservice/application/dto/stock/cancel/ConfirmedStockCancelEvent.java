package com.destiny.stockservice.application.dto.stock.cancel;

import java.util.List;
import java.util.UUID;

public record ConfirmedStockCancelEvent(
    UUID sagaId,
    UUID orderId,
    List<StockCancelItem> items
) {
    public record StockCancelItem(
        UUID productId,
        Integer stock
    ) { }
}
