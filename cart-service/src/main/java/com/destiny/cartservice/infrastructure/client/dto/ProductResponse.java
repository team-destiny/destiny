package com.destiny.cartservice.infrastructure.client.dto;

import java.util.UUID;
import jakarta.validation.constraints.NotNull;

public record ProductResponse(
    @NotNull
    UUID id,

    @NotNull
    String name,

    @NotNull
    Integer price,

    @NotNull
    UUID brandId,

    @NotNull ProductStatus status,

    @NotNull
    String color,

    @NotNull
    String size
) {}