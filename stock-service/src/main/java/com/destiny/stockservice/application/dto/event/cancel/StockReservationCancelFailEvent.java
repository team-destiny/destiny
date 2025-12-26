package com.destiny.stockservice.application.dto.event.cancel;

import java.util.UUID;

public record StockReservationCancelFailEvent(
    UUID sagaId,
    String message
) { }
