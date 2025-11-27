package com.gbg.brandservice.presentation.dto.request;

import java.util.UUID;

public record BrandCreateRequest(
    String brandName,
    UUID managerId
) {

}
