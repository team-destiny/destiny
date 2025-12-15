package com.destiny.orderservice.infrastructure.messaging.event.result;

import java.util.UUID;

public record OrderCancelFailedEvent(
    UUID orderId,
    String message
) {

}
