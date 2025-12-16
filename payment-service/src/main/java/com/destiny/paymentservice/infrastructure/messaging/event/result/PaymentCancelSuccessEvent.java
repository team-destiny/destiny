package com.destiny.paymentservice.infrastructure.messaging.event.result;

import java.util.UUID;
import lombok.Builder;

@Builder
public record PaymentCancelSuccessEvent(
    UUID sagaId,
    UUID orderId
) {

}
