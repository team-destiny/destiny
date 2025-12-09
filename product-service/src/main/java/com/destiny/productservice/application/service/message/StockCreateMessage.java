package com.destiny.productservice.application.service.message;

import java.util.UUID;

public record StockCreateMessage(
    UUID productId,
    Integer quantity
) { }
