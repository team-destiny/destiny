package com.destiny.sagaorchestrator.infrastructure.messaging.event.command;

import java.util.UUID;

public record PaymentCreateCommand(
    UUID orderId,
    UUID userId,
    Integer finalAmount
) {

}
