package com.destiny.paymentservice.infrastructure.messaging.event.command;

import java.util.UUID;

public record PaymentCommand(
    UUID orderId,
    UUID userId,
    Integer finalAmount
) {

}
