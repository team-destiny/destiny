package com.destiny.productservice.application.command;

import java.util.UUID;

public record UpdateProductCommand(
    UUID id,
    String name,
    Long price,
    String brand,
    String productStatus,
    String color,
    String size
) { }
