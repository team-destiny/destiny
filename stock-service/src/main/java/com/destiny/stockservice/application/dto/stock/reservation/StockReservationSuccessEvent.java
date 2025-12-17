package com.destiny.stockservice.application.dto.stock.reservation;

import java.util.List;
import java.util.UUID;

public record StockReservationSuccessEvent(
    UUID orderId,
    List<StockReservationItem> items
) { }
