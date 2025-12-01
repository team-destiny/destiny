package com.destiny.brandservice.application.service;

import com.destiny.brandservice.domain.entity.Brand;
import com.destiny.brandservice.domain.repository.BrandRepository;
import com.destiny.brandservice.infrastructure.exception.BrandError;
import com.destiny.brandservice.presentation.dto.request.BrandCreateRequest;
import com.destiny.brandservice.presentation.dto.request.BrandUpdateRequest;
import com.destiny.brandservice.presentation.dto.response.BrandResponse;
import com.destiny.global.exception.BizException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;

    @Transactional
    public UUID createBrand(BrandCreateRequest req) {

        Brand brand = Brand.of(req.managerId(), req.brandName());

        return brandRepository.create(brand).getBrandId();
    }

    @Transactional(readOnly = true)
    public BrandResponse getBrand(UUID brandId) {

        Brand brand = findBrand(brandId);

        return new BrandResponse(
            brand.getBrandId(),
            brand.getManagerId(),
            brand.getBrandName()
        );
    }

    @Transactional
    public UUID updateBrand(UUID brandId, BrandUpdateRequest req) {

        Brand brand = findBrand(brandId);

        if (req.managerId() != null) {
            brand.updateManagerId(req.managerId());
        }

        if (req.brandName() != null) {
            brand.updateBrandName(req.brandName());
        }

        Brand updateBrand = brandRepository.update(brand);

        return updateBrand.getBrandId();
    }

    @Transactional
    public void deleteBrand(UUID brandId) {

        Brand brand = findBrand(brandId);

        if (brand.isDeleted()) {
            throw new BizException(BrandError.BRAND_NOT_FOUND);
        }

        // TODO : 베이스엔티티 유저 아이디 들어가는 부분 Long -> UUID 수정 필요
        brand.markDeleted(1L);
    }

    private Brand findBrand(UUID brandId) {

        return brandRepository.findBrand(brandId).orElseThrow(
            // TODO : 공통 모듈 상속 받은 이후 따로 예외 코드 정리 해야함.
            () -> new BizException(BrandError.BRAND_NOT_FOUND)
        );
    }
}
