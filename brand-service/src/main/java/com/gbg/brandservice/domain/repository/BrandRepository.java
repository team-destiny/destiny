package com.gbg.brandservice.domain.repository;

import com.gbg.brandservice.domain.entity.Brand;
import java.util.Optional;
import java.util.UUID;

public interface BrandRepository {

    Brand create(Brand brand);

    Optional<Brand> findBrand(UUID brandId);
}
