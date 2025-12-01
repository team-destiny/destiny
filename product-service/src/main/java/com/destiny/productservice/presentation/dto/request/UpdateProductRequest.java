package com.destiny.productservice.presentation.dto.request;

import com.destiny.productservice.application.command.UpdateProductCommand;
import java.util.UUID;

public record UpdateProductRequest(
    UUID id,
    String name,
    Long price,
    String brand,
    String productStatus,
    String color,
    String size
) {
    public UpdateProductCommand toCommand() {
        return new UpdateProductCommand(id, name, price, brand, productStatus, color, size);
    }
}
