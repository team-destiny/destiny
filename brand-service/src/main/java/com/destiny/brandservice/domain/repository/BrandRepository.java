package com.destiny.brandservice.domain.repository;

import com.destiny.brandservice.domain.entity.Brand;
import com.destiny.brandservice.presentation.dto.response.BrandResponse;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BrandRepository {

    Brand create(Brand brand);

    Optional<Brand> findBrand(UUID brandId);

    Brand update(Brand brand);

    List<Brand> findAll();

    List<Brand> findByName(String brandName);
}
