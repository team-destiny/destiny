package com.destiny.sagaorchestrator.infrastructure.messaging.event.request;

import java.util.UUID;

public record PaymentSuccess(
    UUID orderId
) {

}
