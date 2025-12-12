package com.destiny.sagaorchestrator.infrastructure.messaging.event.result;

import java.util.UUID;

public record ProductValidationResult(
    UUID orderId,
    UUID productId,
    Integer stock,
    UUID brandId,
    Integer price
) {

}
