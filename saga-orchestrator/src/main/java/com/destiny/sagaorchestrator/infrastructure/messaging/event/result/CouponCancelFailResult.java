package com.destiny.sagaorchestrator.infrastructure.messaging.event.result;

import java.util.UUID;

public record CouponCancelFailResult(
    UUID sagaId,
    String message
) {

}
