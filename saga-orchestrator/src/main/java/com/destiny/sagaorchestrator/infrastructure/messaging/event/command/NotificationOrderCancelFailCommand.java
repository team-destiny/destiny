package com.destiny.sagaorchestrator.infrastructure.messaging.event.command;

import java.util.UUID;

public record NotificationOrderCancelFailCommand(
    UUID orderId,
    UUID userId,
    String failStep,
    String failReason
) {

}
