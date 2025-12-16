package com.destiny.paymentservice.infrastructure.messaging.event.command;

import java.util.UUID;

public record PaymentCancelCommand(
    UUID sagaId,
    UUID orderId,
    UUID userId,
    Integer finalAmount
) {

}
