package com.destiny.couponservice.infrastructure.messaging.event.command;

import java.util.UUID;

public record CouponCancelFailEvent(
    UUID sagaId,
    String message
) {

}
