package com.destiny.stockservice.application.dto;

import java.util.UUID;

public record StockReservationCancelItem(
    UUID productId,
    Integer orderedQuantity
) { }
