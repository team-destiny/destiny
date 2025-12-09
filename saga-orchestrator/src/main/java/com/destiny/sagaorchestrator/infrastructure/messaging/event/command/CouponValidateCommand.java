package com.destiny.sagaorchestrator.infrastructure.messaging.event.command;

import java.util.UUID;

public record CouponValidateCommand(
    UUID orderId,
    UUID couponId,
    Integer originalAmount
) {

}
