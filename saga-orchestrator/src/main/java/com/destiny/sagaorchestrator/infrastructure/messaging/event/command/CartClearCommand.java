package com.destiny.sagaorchestrator.infrastructure.messaging.event.command;

import java.util.UUID;

public record CartClearCommand(
    UUID cartId
) {

}
