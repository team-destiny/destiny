package com.destiny.couponservice.infrastructure.messaging.event.result;

import java.util.UUID;
import lombok.Builder;

@Builder
public record CouponValidateSuccessEvent(
    UUID couponId,
    Integer finalAmount
) {

}
