package com.destiny.productservice.application.dto;

import java.util.List;
import java.util.UUID;

public record ProductValidationRequest(
    List<UUID> productIdList
) { }
