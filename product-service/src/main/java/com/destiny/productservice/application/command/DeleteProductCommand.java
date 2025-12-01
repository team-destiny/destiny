package com.destiny.productservice.application.command;

import java.util.UUID;

public record DeleteProductCommand(
    UUID id
) { }
