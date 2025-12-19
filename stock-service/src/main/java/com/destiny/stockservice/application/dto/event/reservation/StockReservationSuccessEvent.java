package com.destiny.stockservice.application.dto.event.reservation;

import java.util.List;
import java.util.UUID;

public record StockReservationSuccessEvent(
    UUID orderId,
    List<StockReservationItem> items
) { }
