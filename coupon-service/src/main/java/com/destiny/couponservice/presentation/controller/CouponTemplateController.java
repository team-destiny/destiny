package com.destiny.couponservice.presentation.controller;

import com.destiny.couponservice.presentation.dto.request.CouponTemplateCreateRequest;
import com.destiny.couponservice.presentation.dto.response.CouponTemplateCreateResponse;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/coupon-templates")
public class CouponTemplateController {

    @PostMapping
    public ResponseEntity<CouponTemplateCreateResponse> createTemplate(
        @Valid @RequestBody CouponTemplateCreateRequest request
    ){

        // (임시) 서비스 로직 없이 더미 응답 생성
        CouponTemplateCreateResponse response = CouponTemplateCreateResponse.builder()
            .id(UUID.randomUUID())
            .code(request.getCode())
            .name(request.getName())
            .discountType(request.getDiscountType())
            .discountValue(request.getDiscountValue())
            .minOrderAmount(request.getMinOrderAmount())
            .availableFrom(request.getAvailableFrom())
            .availableTo(request.getAvailableTo())
            .isDuplicateUsable(
                request.getIsDuplicateUsable() != null
                    ? request.getIsDuplicateUsable()
                    : false
            )
            .maxDiscountAmount(request.getMaxDiscountAmount())
            .dailyIssueLimit(request.getDailyIssueLimit())
            .perUserTotalLimit(request.getPerUserTotalLimit())
            .createdAt(LocalDateTime.now())
            .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
