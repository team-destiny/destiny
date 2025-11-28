package com.destiny.couponservice.presentation.controller;

import com.destiny.couponservice.domain.enums.DiscountType;
import com.destiny.couponservice.presentation.dto.request.CouponTemplateCreateRequest;
import com.destiny.couponservice.presentation.dto.response.CouponTemplateCreateResponse;
import com.destiny.couponservice.presentation.dto.response.CouponTemplateGetResponse;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    ) {

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

    @GetMapping("/{templateId}")
    public ResponseEntity<CouponTemplateGetResponse> getTemplate(
        @PathVariable UUID templateId
    ) {
        // TODO 이후 Service 에서 실제 DB 조회로 변경

        CouponTemplateGetResponse response = CouponTemplateGetResponse.builder()
            .id(templateId)
            .code("WELCOME10")
            .name("신규회원 할인쿠폰")
            .discountType(DiscountType.RATE)
            .discountValue(10)
            .minOrderAmount(0)
            .isDuplicateUsable(false)
            .maxDiscountAmount(5000)
            .dailyIssueLimit(100)
            .perUserTotalLimit(1)
            .availableFrom(LocalDateTime.of(2025, 1, 1, 0, 0))
            .availableTo(LocalDateTime.of(2025, 12, 31, 23, 59))
            .createdAt(LocalDateTime.of(2025, 1, 1, 0, 0))
            .updatedAt(LocalDateTime.of(2025, 1, 10, 12, 0))
            .build();

        return ResponseEntity.ok(response);
    }


}
