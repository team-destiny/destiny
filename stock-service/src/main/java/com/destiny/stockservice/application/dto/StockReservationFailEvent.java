package com.destiny.stockservice.application.dto;

import java.util.UUID;

public record StockReservationFailEvent(
    UUID orderId
) { }