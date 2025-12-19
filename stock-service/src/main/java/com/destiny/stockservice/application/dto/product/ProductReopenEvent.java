package com.destiny.stockservice.application.dto.product;

import java.util.List;
import java.util.UUID;

public record ProductReopenEvent(
    List<UUID> productIds
) { }

