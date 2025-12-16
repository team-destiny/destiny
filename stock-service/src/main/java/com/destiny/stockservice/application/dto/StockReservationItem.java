package com.destiny.stockservice.application.dto;

import java.util.UUID;

public record StockReservationItem(
    UUID productId,
    Integer orderedQuantity
) {

}
