package com.destiny.stockservice.application.dto;

import java.util.UUID;

public record StockCancelSuccessEvent(
    UUID sagaId
) { }
