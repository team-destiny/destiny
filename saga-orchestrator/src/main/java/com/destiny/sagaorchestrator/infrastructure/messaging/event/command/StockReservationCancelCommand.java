package com.destiny.sagaorchestrator.infrastructure.messaging.event.command;

import java.util.List;
import java.util.UUID;

public record StockReservationCancelCommand(
    UUID orderId,
    List<StockReservationItem> items
) {

}
