package com.destiny.sagaorchestrator.infrastructure.messaging.event.result;

import java.util.UUID;

public record CouponUseFailResult(
    UUID orderId,
    UUID couponId,
    String errorCode,
    String errorMessage
) {

}
