package com.destiny.sagaorchestrator.infrastructure.messaging.event.result;

import java.util.UUID;

public record ProductValidationMessageResult(
    UUID productId,
    UUID brandId,
    Integer price
) {

}
