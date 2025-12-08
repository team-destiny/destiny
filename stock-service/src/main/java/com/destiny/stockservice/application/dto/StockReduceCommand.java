package com.destiny.stockservice.application.dto;

import java.util.Map;
import java.util.UUID;

public record StockReduceCommand(
    UUID orderId,
    Map<UUID, Integer> orderedProducts
) { }
