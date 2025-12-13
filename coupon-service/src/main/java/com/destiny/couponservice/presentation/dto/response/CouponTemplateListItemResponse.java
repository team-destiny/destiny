package com.destiny.couponservice.presentation.dto.response;

import com.destiny.couponservice.domain.entity.CouponTemplate;
import com.destiny.couponservice.domain.enums.DiscountType;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CouponTemplateListItemResponse {

    private UUID id;
    private String code;
    private String name;

    private DiscountType discountType;
    private Integer discountValue;

    private Integer minOrderAmount;
    private Integer maxDiscountAmount;

    private LocalDateTime availableTo;

    public static CouponTemplateListItemResponse from(CouponTemplate template) {
        return CouponTemplateListItemResponse.builder()
            .id(template.getId())
            .code(template.getCode())
            .name(template.getName())
            .discountType(template.getDiscountType())
            .discountValue(template.getDiscountValue())
            .minOrderAmount(template.getMinOrderAmount())
            .maxDiscountAmount(template.getMaxDiscountAmount())
            .availableTo(template.getAvailableTo())
            .build();
    }
}
