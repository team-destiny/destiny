package com.destiny.sagaorchestrator.infrastructure.messaging.event.command;

import java.util.UUID;

public record StockReduceItem(
    UUID productId,
    Integer stock
) {

}
