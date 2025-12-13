package com.destiny.couponservice.presentation.controller;

import com.destiny.couponservice.application.service.IssuedCouponService;
import com.destiny.couponservice.domain.enums.IssuedCouponStatus;
import com.destiny.couponservice.infrastructure.security.util.SecurityUtils;
import com.destiny.couponservice.presentation.advice.IssuedCouponSuccessCode;
import com.destiny.couponservice.presentation.dto.request.CouponCancelRequest;
import com.destiny.couponservice.presentation.dto.response.IssuedCouponDetailResponse;
import com.destiny.couponservice.presentation.dto.response.IssuedCouponSearchResponse;
import com.destiny.global.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/v1")
@PreAuthorize("isAuthenticated()")
public class IssuedCouponController {

    private final IssuedCouponService issuedCouponService;

    @PostMapping("/coupons/{couponTemplateId}/issue")
    public ResponseEntity<ApiResponse<IssuedCouponDetailResponse>> issueCoupon(
        @PathVariable UUID couponTemplateId
    ) {
        UUID userId = SecurityUtils.getCurrentUserId();
        IssuedCouponDetailResponse response =
            issuedCouponService.issueCoupon(userId, couponTemplateId);

        return ResponseEntity
            .status(IssuedCouponSuccessCode.COUPON_CREATE.getStatus())
            .body(ApiResponse.success(IssuedCouponSuccessCode.COUPON_CREATE, response));
    }

    @GetMapping("/issued-coupons/{issuedCouponId}")
    public ResponseEntity<ApiResponse<IssuedCouponDetailResponse>> getIssuedCoupon(
        @PathVariable UUID issuedCouponId
    ) {
        UUID userId = SecurityUtils.getCurrentUserId();
        IssuedCouponDetailResponse response =
            issuedCouponService.getIssuedCoupon(userId, issuedCouponId);

        return ResponseEntity
            .status(IssuedCouponSuccessCode.COUPON_GET.getStatus())
            .body(ApiResponse.success(IssuedCouponSuccessCode.COUPON_GET, response));
    }

    @GetMapping("/issued-coupons")
    public ResponseEntity<ApiResponse<IssuedCouponSearchResponse>> getIssuedCoupons(
        @RequestParam(defaultValue = "AVAILABLE") IssuedCouponStatus status,
        Pageable pageable
    ) {
        UUID userId = SecurityUtils.getCurrentUserId();
        IssuedCouponSearchResponse response =
            issuedCouponService.getIssuedCoupons(userId, status, pageable);

        return ResponseEntity
            .status(IssuedCouponSuccessCode.COUPON_LIST_GET.getStatus())
            .body(ApiResponse.success(IssuedCouponSuccessCode.COUPON_LIST_GET, response));
    }

    @PatchMapping("/issued-coupons/{issuedCouponId}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelCouponUse(
        @PathVariable UUID issuedCouponId,
        @Valid @RequestBody CouponCancelRequest request
    ) {
        UUID userId = SecurityUtils.getCurrentUserId();
        issuedCouponService.cancelCouponUse(userId, issuedCouponId, request.getOrderId());

        return ResponseEntity
            .status(IssuedCouponSuccessCode.COUPON_CANCEL.getStatus())
            .body(ApiResponse.success(IssuedCouponSuccessCode.COUPON_CANCEL));
    }
}
