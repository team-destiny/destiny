package com.destiny.sagaorchestrator.infrastructure.messaging.event.result;

import java.util.UUID;

public record ProductValidateResult(
    UUID productId,
    UUID itemPromotionId,
    Integer unitPrice,
    Integer finalPrice,
    Integer stock,
    Integer itemDiscountAmount
) {

}
