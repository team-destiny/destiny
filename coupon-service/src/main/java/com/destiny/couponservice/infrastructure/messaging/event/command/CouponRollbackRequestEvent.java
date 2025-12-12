package com.destiny.couponservice.infrastructure.messaging.event.command;

import java.util.UUID;

public record CouponRollbackRequestEvent(
    UUID sagaId,
    UUID couponId
) {

}
