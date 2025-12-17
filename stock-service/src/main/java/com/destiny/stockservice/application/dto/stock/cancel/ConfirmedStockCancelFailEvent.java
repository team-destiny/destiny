package com.destiny.stockservice.application.dto.stock.cancel;

import java.util.UUID;

public record ConfirmedStockCancelFailEvent(
    UUID sagaId,
    String message
) { }
