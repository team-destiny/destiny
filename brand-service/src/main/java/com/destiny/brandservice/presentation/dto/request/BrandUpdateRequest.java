package com.destiny.brandservice.presentation.dto.request;

import java.util.UUID;

public record BrandUpdateRequest(
    UUID managerId,
    String brandName
) {

}
