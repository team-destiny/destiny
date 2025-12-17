package com.destiny.sagaorchestrator.infrastructure.messaging.event.command;

import java.util.UUID;

public record NotificationOrderCancelCommand(
    UUID orderId,
    UUID userId,
    Integer finalAmount,
    String message
) {

}
