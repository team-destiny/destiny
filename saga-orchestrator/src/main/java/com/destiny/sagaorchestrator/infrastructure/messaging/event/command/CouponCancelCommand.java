package com.destiny.sagaorchestrator.infrastructure.messaging.event.command;

import java.util.UUID;

public record CouponCancelCommand(
    UUID sagaId,
    UUID orderId,
    UUID userId,
    UUID couponId
) {

}
