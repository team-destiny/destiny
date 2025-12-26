package com.destiny.stockservice.application.dto.event.reservation;

import java.util.List;
import java.util.UUID;

public record StockReservationCancelEvent(
    UUID sagaId,
    UUID orderId,
    List<StockReservationCancelItem> items
) {
    public record StockReservationCancelItem(
        UUID productId,
        Integer orderedQuantity
    ) { }
}
