package com.destiny.stockservice.application.dto;

import java.util.List;
import java.util.UUID;

public record StockReduceSuccessResult(
    UUID orderId,
    List<StockReduceItem> items
) { }