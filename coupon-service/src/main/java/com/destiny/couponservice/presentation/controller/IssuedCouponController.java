package com.destiny.couponservice.presentation.controller;

import com.destiny.couponservice.application.service.IssuedCouponService;
import com.destiny.couponservice.domain.enums.IssuedCouponStatus;
import com.destiny.couponservice.infrastructure.security.util.SecurityUtils;
import com.destiny.couponservice.presentation.dto.request.CouponCancelRequest;
import com.destiny.couponservice.presentation.dto.response.IssuedCouponDetailResponse;
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
    public ResponseEntity<IssuedCouponDetailResponse> issueCoupon(
        @PathVariable UUID couponTemplateId
    ) {
        UUID userId = SecurityUtils.getCurrentUserId();
        IssuedCouponDetailResponse response = issuedCouponService.issueCoupon(userId,
            couponTemplateId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/issued-coupons/{issuedCouponId}")
    public ResponseEntity<IssuedCouponDetailResponse> getIssuedCoupon(
        @PathVariable UUID issuedCouponId
    ) {
        UUID userId = SecurityUtils.getCurrentUserId();
        IssuedCouponDetailResponse response = issuedCouponService.getIssuedCoupon(userId,
            issuedCouponId);
        return ResponseEntity.ok(response);
    }


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


    @PostMapping("/issued-coupons/{issuedCouponId}/cancel")
    public ResponseEntity<String> cancelCouponUse(
        @PathVariable UUID issuedCouponId,
        @Valid @RequestBody CouponCancelRequest request
    ) {
        UUID userId = SecurityUtils.getCurrentUserId();
        issuedCouponService.cancelCouponUse(userId, issuedCouponId, request.getOrderId());
        return ResponseEntity.ok("쿠폰이 사용이 취소되었습니다");
    }
}
