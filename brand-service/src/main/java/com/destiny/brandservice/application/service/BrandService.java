package com.destiny.brandservice.application.service;

import com.destiny.brandservice.domain.entity.Brand;
import com.destiny.brandservice.domain.repository.BrandRepository;
import com.destiny.brandservice.presentation.dto.request.BrandCreateRequest;
import com.destiny.brandservice.presentation.dto.response.BrandResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;


    public UUID createBrand(BrandCreateRequest req) {

        Brand brand = Brand.of(req.managerId(), req.brandName());

        return brandRepository.create(brand).getBrandId();
    }

    public BrandResponse getBrand(UUID brandId) {

        Brand brand = findBrand(brandId);

        return new BrandResponse(
            brand.getBrandId(),
            brand.getManagerId(),
            brand.getBrandName()
        );
    }

    private Brand findBrand(UUID brandId) {

        return brandRepository.findBrand(brandId).orElseThrow(
            // TODO : 공통 모듈 상속 받은 이후 따로 예외 코드 정리 해야함.
            () -> new RuntimeException("Brand Id가 유효하지 않습니다.")
        );
    }
}
