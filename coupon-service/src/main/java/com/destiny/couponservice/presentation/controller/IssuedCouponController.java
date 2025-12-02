package com.destiny.couponservice.presentation.controller;

import com.destiny.couponservice.application.service.IssuedCouponService;
import com.destiny.couponservice.domain.enums.IssuedCouponStatus;
import com.destiny.couponservice.presentation.dto.response.IssuedCouponResponseDto;
import com.destiny.couponservice.presentation.dto.response.IssuedCouponSearchResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class IssuedCouponController {

    private final IssuedCouponService issuedCouponService;

    /**
     * 쿠폰 발급
     * POST /v1/coupons/{couponTemplateId}/issue
     */
    @PostMapping("/coupons/{couponTemplateId}/issue")
    public ResponseEntity<IssuedCouponResponseDto> issueCoupon(
        @PathVariable UUID couponTemplateId,
        @RequestHeader("X-USER-ID") UUID userId   // TODO: 유저 서비스 완성 되면 수정
    ) {
        IssuedCouponResponseDto response = issuedCouponService.issueCoupon(userId,
            couponTemplateId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 내가 발급받은 쿠폰 단건 조회
     * GET /v1/issued-coupons/{issuedCouponId}
     */
    @GetMapping("/issued-coupons/{issuedCouponId}")
    public ResponseEntity<IssuedCouponResponseDto> getIssuedCoupon(
        @PathVariable UUID issuedCouponId,
        @RequestHeader("X-USER-ID") UUID userId   // TODO: 유저 서비스 완성 되면 수정
    ) {
        IssuedCouponResponseDto response = issuedCouponService.getIssuedCoupon(userId,
            issuedCouponId);
        return ResponseEntity.ok(response);
    }

    /**
     * 내가 발급받은 쿠폰 목록 조회
     * GET /v1/issued-coupons?status=AVAILABLE ( 사용가능한 쿠폰 목록조회 )
     */
    @GetMapping("/issued-coupons")
    public ResponseEntity<IssuedCouponSearchResponse> getIssuedCoupons(
        @RequestHeader("X-USER-ID") UUID userId,  // TODO: 유저 서비스 완성 되면 수정
        @RequestParam(defaultValue = "AVAILABLE") IssuedCouponStatus status,
        Pageable pageable
    ) {
        IssuedCouponSearchResponse response =
            issuedCouponService.getIssuedCoupons(userId, status, pageable);

        return ResponseEntity.ok(response);
    }
}
