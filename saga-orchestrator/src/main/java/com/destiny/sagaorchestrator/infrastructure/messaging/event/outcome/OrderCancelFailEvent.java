package com.destiny.sagaorchestrator.infrastructure.messaging.event.outcome;

import java.util.UUID;

public record OrderCancelFailEvent(
    UUID orderId,
    String message
) {

}
