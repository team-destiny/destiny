package com.destiny.orderservice.infrastructure.messaging.event.result;

import java.util.UUID;

public record OrderCancelSuccessEvent(
    UUID orderId
) {

}
