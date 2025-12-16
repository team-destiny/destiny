package com.destiny.orderservice.infrastructure.messaging.event.outbound;

import java.util.UUID;

public record OrderCancelRequestEvent(

    UUID orderId
) { }
