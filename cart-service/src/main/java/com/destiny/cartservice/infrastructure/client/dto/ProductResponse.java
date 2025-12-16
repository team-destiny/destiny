package com.destiny.cartservice.infrastructure.client.dto;

import java.util.UUID;

public record ProductResponse(
    UUID id,
    String name,
    Integer price,
    UUID brandId,
    String status,
    String color,
    String size
) {}