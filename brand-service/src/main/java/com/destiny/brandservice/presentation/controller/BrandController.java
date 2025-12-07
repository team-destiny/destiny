package com.destiny.brandservice.presentation.controller;

import com.destiny.brandservice.application.service.BrandService;
import com.destiny.brandservice.infrastructure.auth.CustomUserDetails;
import com.destiny.brandservice.presentation.dto.request.BrandCreateRequest;
import com.destiny.brandservice.presentation.dto.request.BrandUpdateRequest;
import com.destiny.brandservice.presentation.dto.response.BrandResponse;
import com.destiny.brandservice.presentation.dto.response.OrderItemForBrandResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/brands")
public class BrandController {

    private final BrandService brandService;

    @PostMapping
    public ResponseEntity<UUID> createBrand(
        @RequestBody @Valid BrandCreateRequest req,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UUID brand = brandService.createBrand(userDetails, req);

        return ResponseEntity.status(HttpStatus.CREATED).body(brand);
    }

    @GetMapping
    public ResponseEntity<List<BrandResponse>> brandList(
        @RequestParam(required = false) String brandName
    ) {
        List<BrandResponse> brands = brandService.brandList(brandName);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(brands);
    }

    @GetMapping("/{brandId}")
    public ResponseEntity<BrandResponse> getBrand(
        @PathVariable UUID brandId
    ) {
        BrandResponse brand = brandService.getBrand(brandId);

        return ResponseEntity.status(HttpStatus.OK).body(brand);
    }

    @PatchMapping("/{brandId}")
    public ResponseEntity<UUID> updateBrand(
        @PathVariable UUID brandId,
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestBody BrandUpdateRequest req
    ) {

        UUID brand = brandService.updateBrand(userDetails ,brandId, req);

        return ResponseEntity.status(HttpStatus.OK).body(brand);
    }

    @DeleteMapping("/{brandId}")
    public ResponseEntity<String> deleteBrand(
        @PathVariable UUID brandId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        brandService.deleteBrand(userDetails, brandId);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body("브랜드 삭제가 완료되었습니다.");
    }

    @GetMapping("/{brandId}/orders")
    public ResponseEntity<List<OrderItemForBrandResponse>> getMyOrders(
        @PathVariable UUID brandId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        // TODO : 헤더로 유저 넘어오면 스프링 시큐리티 설정 이후 유저 검증 진행 해야함 !
        List<OrderItemForBrandResponse> orders = brandService.getMyOrders(userDetails, brandId);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(orders);
    }

}
