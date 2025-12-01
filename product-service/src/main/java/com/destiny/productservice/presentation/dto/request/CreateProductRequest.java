package com.destiny.productservice.presentation.dto.request;

import com.destiny.productservice.application.command.CreateProductCommand;

public record CreateProductRequest(
    String name,
    Long price,
    String brand,
    String color,
    String size
) {
    public CreateProductCommand toCommand() {
        return new CreateProductCommand(name, price, brand, color, size);
    }
}
