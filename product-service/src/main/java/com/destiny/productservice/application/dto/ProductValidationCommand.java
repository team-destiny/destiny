package com.destiny.productservice.application.dto;

import java.util.List;
import java.util.UUID;

public record ProductValidationCommand(
    UUID orderId,
    List<UUID> productIds
) { }
