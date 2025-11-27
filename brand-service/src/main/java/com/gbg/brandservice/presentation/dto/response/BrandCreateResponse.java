package com.gbg.brandservice.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record BrandCreateResponse(
    UUID brandId,
    UUID managerId,
    String brandName,
    LocalDateTime createdAt,
    UUID createdBy,
    LocalDateTime updatedAt,
    UUID updatedBy
) {

}
