package com.destiny.stockservice.application.dto.event.cancel;

import java.util.UUID;

public record ConfirmedStockCancelFailEvent(
    UUID sagaId,
    String message
) { }
