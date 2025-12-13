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
public class IssuedCouponDetailResponse {

    private UUID id;
    private UUID userId;
    private UUID couponTemplateId;
    private UUID orderId;
    private IssuedCouponStatus status;
    private LocalDateTime issuedAt;
    private LocalDateTime expiredAt;
    private LocalDateTime usedAt;
    private String code;
    private String name;
    private DiscountType discountType;
    private Integer discountValue;
    private Integer minOrderAmount;
    private Integer maxDiscountAmount;

    public static IssuedCouponDetailResponse from(IssuedCoupon issuedCoupon, CouponTemplate template) {
        return IssuedCouponDetailResponse.builder()
            .id(issuedCoupon.getId())
            .userId(issuedCoupon.getUserId())
            .orderId(issuedCoupon.getOrderId())
            .couponTemplateId(issuedCoupon.getCouponTemplateId())
            .status(issuedCoupon.getStatus())
            .issuedAt(issuedCoupon.getIssuedAt())
            .expiredAt(issuedCoupon.getExpiredAt())
            .usedAt(issuedCoupon.getUsedAt())
            .code(template.getCode())
            .name(template.getName())
            .discountType(template.getDiscountType())
            .discountValue(template.getDiscountValue())
            .minOrderAmount(template.getMinOrderAmount())
            .maxDiscountAmount(template.getMaxDiscountAmount())
            .build();
    }
}
