package com.destiny.paymentservice.infrastructure.messaging.event.result;

import java.util.UUID;
import lombok.Builder;

@Builder
public record PaymentFailEvent(
    UUID orderId,
    String errorCode,
    String errorMessage
) {

}
