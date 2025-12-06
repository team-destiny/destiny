package com.destiny.sagaorchestrator.infrastructure.messaging.event.result;

import java.util.UUID;

public record CouponUseFailResult(
    UUID orderId,
    String message
) {

}
