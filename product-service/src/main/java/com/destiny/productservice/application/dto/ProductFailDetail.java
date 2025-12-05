package com.destiny.productservice.application.dto;

import java.util.UUID;

public record ProductFailDetail(
    UUID id,
    String failMessage
) { }
