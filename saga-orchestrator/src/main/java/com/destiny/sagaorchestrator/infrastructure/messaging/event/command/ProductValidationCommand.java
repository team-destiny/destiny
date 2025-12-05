package com.destiny.sagaorchestrator.infrastructure.messaging.event.command;

import java.util.List;
import java.util.UUID;

public record ProductValidationCommand(
    UUID orderId,
    List<UUID> productIds
) {

}
