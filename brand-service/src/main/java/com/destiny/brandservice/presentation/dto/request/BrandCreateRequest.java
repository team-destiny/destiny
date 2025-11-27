package com.destiny.brandservice.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record BrandCreateRequest(
    @NotNull @Size(min = 2) String brandName,
    @NotNull UUID managerId
) {

}
