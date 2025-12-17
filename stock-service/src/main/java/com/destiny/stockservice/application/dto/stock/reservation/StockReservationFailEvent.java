package com.destiny.stockservice.application.dto.stock.reservation;

import java.util.UUID;

public record StockReservationFailEvent(
    UUID orderId
) { }