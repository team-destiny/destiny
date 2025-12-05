package com.destiny.couponservice.presentation.controller;

import com.destiny.couponservice.application.service.IssuedCouponService;
import com.destiny.couponservice.domain.enums.IssuedCouponStatus;
import com.destiny.couponservice.infrastructure.security.util.SecurityUtils;
import com.destiny.couponservice.presentation.dto.request.CouponCancelRequest;
import com.destiny.couponservice.presentation.dto.request.CouponUseRequest;
import com.destiny.couponservice.presentation.dto.response.CouponUseResponse;
import com.destiny.couponservice.presentation.dto.response.IssuedCouponResponseDto;
import com.destiny.couponservice.presentation.dto.response.IssuedCouponSearchResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
@PreAuthorize("isAuthenticated()")
public class IssuedCouponController {

    private final IssuedCouponService issuedCouponService;

    /**
     * 쿠폰 발급 POST /v1/coupons/{couponTemplateId}/issue
     */
    @PostMapping("/coupons/{couponTemplateId}/issue")
    public ResponseEntity<IssuedCouponResponseDto> issueCoupon(
        @PathVariable UUID couponTemplateId
    ) {
        UUID userId = SecurityUtils.getCurrentUserId();
        IssuedCouponResponseDto response = issuedCouponService.issueCoupon(userId,
            couponTemplateId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 내가 발급받은 쿠폰 단건 조회 GET /v1/issued-coupons/{issuedCouponId}
     */
    @GetMapping("/issued-coupons/{issuedCouponId}")
    public ResponseEntity<IssuedCouponResponseDto> getIssuedCoupon(
        @PathVariable UUID issuedCouponId
    ) {
        UUID userId = SecurityUtils.getCurrentUserId();
        IssuedCouponResponseDto response = issuedCouponService.getIssuedCoupon(userId,
            issuedCouponId);
        return ResponseEntity.ok(response);
    }


    /**
     * 내가 발급받은 쿠폰 목록 조회 GET /v1/issued-coupons?status=AVAILABLE
     */
    @GetMapping("/issued-coupons")
    public ResponseEntity<IssuedCouponSearchResponse> getIssuedCoupons(
        @RequestParam(defaultValue = "AVAILABLE") IssuedCouponStatus status,
        Pageable pageable
    ) {
        UUID userId = SecurityUtils.getCurrentUserId();
        IssuedCouponSearchResponse response =
            issuedCouponService.getIssuedCoupons(userId, status, pageable);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/issued-coupons/{issuedCouponId}/use")
    public ResponseEntity<CouponUseResponse> useCoupon(
        @PathVariable UUID issuedCouponId,
        @Valid @RequestBody CouponUseRequest request
    ) {
        UUID userId = SecurityUtils.getCurrentUserId();
        CouponUseResponse response = issuedCouponService.useCoupon(
            userId,
            issuedCouponId,
            request.getOrderId(),
            request.getOrderAmount()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/issued-coupons/{issuedCouponId}/cancel")
    public ResponseEntity<Void> cancelCouponUse(
        @PathVariable UUID issuedCouponId,
        @Valid @RequestBody CouponCancelRequest request
    ) {
        UUID userId = SecurityUtils.getCurrentUserId();
        issuedCouponService.cancelCouponUse(userId, issuedCouponId, request.getOrderId());
        return ResponseEntity.noContent().build();
    }
}
