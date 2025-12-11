package com.destiny.productservice.application.dto;

import java.util.UUID;

public record StockCreateMessage(
    UUID productId,
    Integer quantity
) { }
