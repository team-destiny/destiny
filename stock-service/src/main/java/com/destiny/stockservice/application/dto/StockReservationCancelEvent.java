package com.destiny.stockservice.application.dto;

import java.util.List;
import java.util.UUID;

public record StockReservationCancelEvent(
    UUID sagaId,
    UUID orderId,
    List<StockCancelItem> items
) { }
