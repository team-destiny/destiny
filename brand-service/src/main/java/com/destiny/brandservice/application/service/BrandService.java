package com.destiny.brandservice.application.service;

import com.destiny.brandservice.domain.entity.Brand;
import com.destiny.brandservice.domain.repository.BrandRepository;
import com.destiny.brandservice.infrastructure.auth.CustomUserDetails;
import com.destiny.brandservice.infrastructure.client.OrderClient;
import com.destiny.brandservice.infrastructure.exception.BrandError;
import com.destiny.brandservice.presentation.dto.request.BrandCreateRequest;
import com.destiny.brandservice.presentation.dto.request.BrandUpdateRequest;
import com.destiny.brandservice.presentation.dto.response.BrandResponse;
import com.destiny.brandservice.presentation.dto.response.OrderItemForBrandResponse;
import com.destiny.global.code.CommonErrorCode;
import com.destiny.global.exception.BizException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BrandService {

    private final BrandRepository brandRepository;
    private final OrderClient orderClient;

    @Transactional
    public UUID createBrand(CustomUserDetails userDetails, BrandCreateRequest req) {

        boolean isPartner = userDetails.getUserRole().equalsIgnoreCase("partner")
            || userDetails.getUserRole().equalsIgnoreCase("master");

        if (!isPartner) {
            throw new BizException(CommonErrorCode.ACCESS_DENIED);
        }

        Brand brand = Brand.of(
            userDetails.getUserId(),
            req.brandName());

        return brandRepository.create(brand).getBrandId();
    }

    @Transactional(readOnly = true)
    public BrandResponse getBrand(UUID brandId) {

        // TODO : 상품 feign client 통신 하여 상품 목록 가지고 와야함.

        Brand brand = findBrand(brandId);

        return new BrandResponse(
            brand.getBrandId(),
            brand.getManagerId(),
            brand.getBrandName()
        );
    }

    public List<BrandResponse> brandList(String brandName) {

        if (brandName == null || brandName.isEmpty()) {
            return brandRepository.findAll()
                .stream()
                .map(BrandResponse::from)
                .toList();
        }

        return brandRepository.findByName(brandName)
            .stream()
            .map(BrandResponse::from)
            .toList();
    }

    @Transactional
    public UUID updateBrand(CustomUserDetails userDetails, UUID brandId, BrandUpdateRequest req) {

        Brand brand = findBrand(brandId);

        validateManager(userDetails.getUserId(), brand);

        if (req.brandName() != null) {
            brand.updateBrandName(req.brandName());
        }

        if (req.managerId() != null) {
            brand.updateManagerId(req.managerId());
        }

        Brand updateBrand = brandRepository.update(brand);

        return updateBrand.getBrandId();
    }

    @Transactional
    public void deleteBrand(CustomUserDetails userDetails, UUID brandId) {

        Brand brand = findBrand(brandId);

        if (brand.isDeleted()) {
            throw new BizException(BrandError.BRAND_NOT_FOUND);
        }

        validateManager(userDetails.getUserId(), brand);

        brand.markDeleted(userDetails.getUserId());
        brandRepository.update(brand);
    }

    public List<OrderItemForBrandResponse> getMyOrders(CustomUserDetails userDetails, UUID brandId) {

        log.info("userRole : {}", userDetails.getUserRole());
        log.info("user Id : {}", userDetails.getUserId());

        Brand brand = findBrand(brandId);
        log.info("brand managerId : {}", brand.getManagerId());

        boolean isPartner = userDetails.getUserRole().equalsIgnoreCase("partner")
            || userDetails.getUserRole().equalsIgnoreCase("master");

        if (!isPartner) {
            throw new BizException(CommonErrorCode.ACCESS_DENIED);
        }

        if (!brand.getManagerId().equals(userDetails.getUserId())) {
            throw new BizException(CommonErrorCode.ACCESS_DENIED);
        }

        return orderClient.getItemsForBrand(brandId);
    }

    private Brand findBrand(UUID brandId) {

        return brandRepository.findBrand(brandId).orElseThrow(
            // TODO : 공통 모듈 상속 받은 이후 따로 예외 코드 정리 해야함.
            () -> new BizException(BrandError.BRAND_NOT_FOUND)
        );
    }

    private void validateManager(UUID userId, Brand brand) {

        if (!userId.equals(brand.getManagerId())) {
            throw new BizException(CommonErrorCode.ACCESS_DENIED);
        }
    }
}
