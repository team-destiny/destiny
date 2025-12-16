package com.destiny.sagaorchestrator.infrastructure.messaging.event.request;

import java.util.UUID;

public record PaymentFail(
    UUID orderId,
    String errorCode,
    String errorMessage
) {

}
