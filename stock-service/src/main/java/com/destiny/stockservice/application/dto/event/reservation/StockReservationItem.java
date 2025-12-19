package com.destiny.stockservice.application.dto.event.reservation;

import java.util.UUID;

public record StockReservationItem(
    UUID productId,
    Integer orderedQuantity
) {

}
