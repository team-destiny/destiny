package com.destiny.sagaorchestrator.infrastructure.messaging.event.result;

import java.util.List;
import java.util.UUID;

public record StockReduceSuccessResult(
    UUID orderId,
    List<StockReduceItem> items
) {

}
