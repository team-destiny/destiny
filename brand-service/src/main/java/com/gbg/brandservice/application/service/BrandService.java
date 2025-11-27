package com.gbg.brandservice.application.service;

import com.gbg.brandservice.domain.entity.Brand;
import com.gbg.brandservice.domain.repository.BrandRepository;
import com.gbg.brandservice.presentation.dto.request.BrandCreateRequest;
import com.gbg.brandservice.presentation.dto.response.BrandCreateResponse;
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
}
