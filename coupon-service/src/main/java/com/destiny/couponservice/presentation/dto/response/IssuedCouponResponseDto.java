package com.destiny.couponservice.presentation.dto.response;

import com.destiny.couponservice.domain.entity.CouponTemplate;
import com.destiny.couponservice.domain.entity.IssuedCoupon;
import com.destiny.couponservice.domain.enums.DiscountType;
import com.destiny.couponservice.domain.enums.IssuedCouponStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IssuedCouponResponseDto {

    //  발급 쿠폰 자체 정보
    private UUID id;
    private UUID userId;
    private UUID couponTemplateId;
    private IssuedCouponStatus status;
    private LocalDateTime issuedAt;
    private LocalDateTime expiredAt;
    private LocalDateTime usedAt;
    private UUID orderId;

    //  쿠폰 템플릿 정보
    private String code;
    private String name;
    private DiscountType discountType;
    private Integer discountValue;
    private Integer minOrderAmount;
    private Integer maxDiscountAmount;
    private Integer issueLimit;
    private Integer perUserTotalLimit;
    private LocalDateTime availableFrom;
    private LocalDateTime availableTo;

    public static IssuedCouponResponseDto from(IssuedCoupon issuedCoupon,
        CouponTemplate couponTemplate) {

        return IssuedCouponResponseDto.builder()
            // 발급 쿠폰 정보
            .id(issuedCoupon.getId())
            .userId(issuedCoupon.getUserId())
            .couponTemplateId(issuedCoupon.getCouponTemplateId())
            .status(issuedCoupon.getStatus())
            .issuedAt(issuedCoupon.getIssuedAt())
            .expiredAt(issuedCoupon.getExpiredAt())
            .usedAt(issuedCoupon.getUsedAt())
            .orderId(issuedCoupon.getOrderId())

            // 템플릿 정보
            .code(couponTemplate.getCode())
            .name(couponTemplate.getName())
            .discountType(couponTemplate.getDiscountType())
            .discountValue(couponTemplate.getDiscountValue())
            .minOrderAmount(couponTemplate.getMinOrderAmount())
            .maxDiscountAmount(couponTemplate.getMaxDiscountAmount())
            .issueLimit(couponTemplate.getIssueLimit())
            .availableFrom(couponTemplate.getAvailableFrom())
            .availableTo(couponTemplate.getAvailableTo())
            .build();
    }
}
