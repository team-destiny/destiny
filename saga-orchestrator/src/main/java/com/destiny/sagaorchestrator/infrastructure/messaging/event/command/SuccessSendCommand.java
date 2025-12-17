package com.destiny.sagaorchestrator.infrastructure.messaging.event.command;

import java.util.List;
import java.util.UUID;

public record SuccessSendCommand(
    UUID orderId,
    UUID userId,
    List<OrderItem> items,
    Integer finalAmount
) {

    public record OrderItem(
        UUID productId,
        UUID brandId,
        Integer stock
    ) {

    }
}
