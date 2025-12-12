package com.destiny.sagaorchestrator.infrastructure.messaging.event.result;

import java.util.UUID;

public record ProductValidateFailResult(
    UUID orderId,
    UUID productId,
    String message
) {

}
