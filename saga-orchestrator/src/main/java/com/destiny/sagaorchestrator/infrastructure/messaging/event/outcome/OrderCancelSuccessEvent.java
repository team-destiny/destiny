package com.destiny.sagaorchestrator.infrastructure.messaging.event.outcome;

import java.util.UUID;

public record OrderCancelSuccessEvent(
    UUID orderId
) {

}
