package com.destiny.stockservice.application.dto.event.reservation;

import java.util.UUID;

public record StockReservationCancelSuccessEvent(
    UUID sagaId
) { }
