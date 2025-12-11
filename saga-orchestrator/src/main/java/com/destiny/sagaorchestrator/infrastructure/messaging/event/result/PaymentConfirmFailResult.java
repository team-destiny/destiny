package com.destiny.sagaorchestrator.infrastructure.messaging.event.result;

import java.util.UUID;

public record PaymentConfirmFailResult(
    UUID orderId,
    String errorCode,
    String errorMessage
) {

}
