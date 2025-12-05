package com.destiny.sagaorchestrator.infrastructure.messaging.event.result;

import java.util.UUID;

public record ProductValidateSuccessResult(
    UUID orderId,
    UUID productId,
    UUID brandId,
    Integer price
) {

}
