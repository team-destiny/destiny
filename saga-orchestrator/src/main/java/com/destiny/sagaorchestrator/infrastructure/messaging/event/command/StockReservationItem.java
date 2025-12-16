package com.destiny.sagaorchestrator.infrastructure.messaging.event.command;

import java.util.UUID;

public record StockReservationItem(
    UUID productId,
    Integer orderedQuantity
) {

}
