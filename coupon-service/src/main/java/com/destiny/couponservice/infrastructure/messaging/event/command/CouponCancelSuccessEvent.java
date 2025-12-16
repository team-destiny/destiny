package com.destiny.couponservice.infrastructure.messaging.event.command;

import java.util.UUID;

public record CouponCancelSuccessEvent(
    UUID sagaId
) {
}
