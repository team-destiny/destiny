package com.destiny.brandservice.presentation.dto.response;

import com.destiny.brandservice.domain.entity.Brand;
import java.util.UUID;

public record BrandResponse(
    UUID brandId,
    UUID managerId,
    String brandName
) {

    public static BrandResponse from(Brand brand) {
        return new BrandResponse(
            brand.getBrandId(),
            brand.getManagerId(),
            brand.getBrandName()
        );
    }
}
