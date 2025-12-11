package com.destiny.couponservice.infrastructure.messaging.event.result;

import java.util.UUID;
import lombok.Builder;

@Builder
public record CouponValidateFailEvent(
    UUID orderId,
    UUID couponId,
    String errorCode,
    String errorMessage
) {

}
