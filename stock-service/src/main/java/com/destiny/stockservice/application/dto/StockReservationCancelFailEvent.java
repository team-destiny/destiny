package com.destiny.stockservice.application.dto;

import java.util.UUID;

public record StockReservationCancelFailEvent(
    UUID sagaId,
    String message
) { }
