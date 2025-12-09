package com.destiny.stockservice.application.dto;

import java.util.UUID;

public record StockReduceItem(
    UUID productId,
    Integer stock
) { }
