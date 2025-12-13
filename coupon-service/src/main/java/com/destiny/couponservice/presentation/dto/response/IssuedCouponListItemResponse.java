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
public class IssuedCouponListItemResponse {

    private UUID id;
    private IssuedCouponStatus status;
    private LocalDateTime expiredAt;
    private LocalDateTime usedAt;
    private String name;
    private DiscountType discountType;
    private Integer discountValue;
    private Integer minOrderAmount;
    private Integer maxDiscountAmount;


    public static IssuedCouponListItemResponse from(IssuedCoupon issuedCoupon, CouponTemplate template) {
        return IssuedCouponListItemResponse.builder()
            .id(issuedCoupon.getId())
            .status(issuedCoupon.getStatus())
            .expiredAt(issuedCoupon.getExpiredAt())
            .usedAt(issuedCoupon.getUsedAt())
            .name(template.getName())
            .discountType(template.getDiscountType())
            .discountValue(template.getDiscountValue())
            .minOrderAmount(template.getMinOrderAmount())
            .maxDiscountAmount(template.getMaxDiscountAmount())
            .build();
    }
}
