package com.destiny.sagaorchestrator.infrastructure.messaging.event.command;

import java.util.List;
import java.util.UUID;

public record StockCancelCommand(
    UUID sagaId,
    UUID orderId,
    List<StockCancelItem> items
) {

    public record StockCancelItem(
        UUID productId,
        Integer stock
    ) {

    }

}
