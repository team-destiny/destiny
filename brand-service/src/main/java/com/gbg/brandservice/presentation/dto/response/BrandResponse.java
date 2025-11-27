package com.gbg.brandservice.presentation.dto.response;

import java.util.UUID;

public record BrandResponse(
    UUID brandId,
    UUID managerId,
    String brandName
) {

}
