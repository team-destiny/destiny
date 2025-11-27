package com.gbg.brandservice.domain.repository;

import com.gbg.brandservice.domain.entity.Brand;
import com.gbg.brandservice.presentation.dto.response.BrandCreateResponse;

public interface BrandRepository {

    Brand create(Brand brand);
}
