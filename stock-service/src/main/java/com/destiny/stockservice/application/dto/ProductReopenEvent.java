package com.destiny.stockservice.application.dto;

import java.util.List;
import java.util.UUID;

public record ProductReopenEvent(
    List<UUID> productIds
) { }

