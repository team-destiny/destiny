package com.gbg.brandservice.infrastructure.repository;

import com.gbg.brandservice.domain.entity.Brand;
import com.gbg.brandservice.domain.repository.BrandRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BrandRepositoryImpl implements BrandRepository {

    private final BrandJpaRepository brandJpaRepository;

    @Override
    public Brand create(Brand brand) {

        return brandJpaRepository.save(brand);
    }

    @Override
    public Optional<Brand> findBrand(UUID brandId) {

        return brandJpaRepository.findById(brandId);
    }
}
