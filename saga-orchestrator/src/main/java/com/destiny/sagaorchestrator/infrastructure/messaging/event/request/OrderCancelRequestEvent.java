package com.destiny.sagaorchestrator.infrastructure.messaging.event.request;

import java.util.UUID;

public record OrderCancelRequestEvent(

    UUID orderId,
    String message
) { }
