package com.destiny.stockservice.application.dto.stock.reservation;

import java.util.UUID;

public record StockReservationItem(
    UUID productId,
    Integer orderedQuantity
) {

}
