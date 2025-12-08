package com.destiny.orderservice.infrastructure.messaging.event.result;

import java.util.UUID;

public record OrderCreateFailedEvent(
    UUID sagaId,
    UUID orderId,
    String failReason,
    String errorCode,
    String failedService
) {

}
