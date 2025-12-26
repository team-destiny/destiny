package com.destiny.stockservice.application.dto.event.reservation;

import java.util.UUID;

public record StockReservationCancelFailEvent(
    UUID sagaId,
    String message
) { }
