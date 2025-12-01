package com.destiny.brandservice.infrastructure.repository;

import com.destiny.brandservice.domain.entity.Brand;
import com.destiny.brandservice.domain.repository.BrandRepository;
import com.destiny.brandservice.presentation.dto.response.BrandResponse;
import java.util.List;
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

    @Override
    public Brand update(Brand brand) {

        return brandJpaRepository.save(brand);
    }

    @Override
    public List<Brand> findAll() {

        return brandJpaRepository.findAll();
    }

    @Override
    public List<Brand> findByName(String brandName) {

        return brandJpaRepository.findByBrandNameContainingIgnoreCase(brandName);
    }
}
