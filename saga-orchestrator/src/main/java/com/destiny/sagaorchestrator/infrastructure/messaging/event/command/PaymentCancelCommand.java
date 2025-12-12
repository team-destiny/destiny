package com.destiny.sagaorchestrator.infrastructure.messaging.event.command;

import java.util.UUID;

public record PaymentCancelCommand(
    UUID sagaId,
    UUID orderId,
    Integer finalAmount
) {

}
