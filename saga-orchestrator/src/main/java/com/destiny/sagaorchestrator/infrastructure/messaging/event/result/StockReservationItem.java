package com.destiny.sagaorchestrator.infrastructure.messaging.event.result;

import java.util.UUID;

public record StockReservationItem(
    UUID productId,
    Integer orderedQuantity
) {

}
