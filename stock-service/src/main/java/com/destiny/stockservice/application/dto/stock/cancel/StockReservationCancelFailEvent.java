package com.destiny.stockservice.application.dto.stock.cancel;

import java.util.UUID;

public record StockReservationCancelFailEvent(
    UUID sagaId,
    String message
) { }
