package com.destiny.couponservice.presentation.dto.response;


import com.destiny.couponservice.domain.entity.CouponTemplate;
import com.destiny.couponservice.domain.enums.DiscountType;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CouponTemplateDetailResponse {

    private UUID id;
    private String code;
    private String name;
    private DiscountType discountType;
    private Integer discountValue;
    private Integer minOrderAmount;
    private Integer maxDiscountAmount;
    private Integer issueLimit;
    private LocalDateTime availableFrom;
    private LocalDateTime availableTo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CouponTemplateDetailResponse from(CouponTemplate template) {
        return CouponTemplateDetailResponse.builder()
            .id(template.getId())
            .code(template.getCode())
            .name(template.getName())
            .discountType(template.getDiscountType())
            .discountValue(template.getDiscountValue())
            .minOrderAmount(template.getMinOrderAmount())
            .maxDiscountAmount(template.getMaxDiscountAmount())
            .issueLimit(template.getIssueLimit())
            .availableFrom(template.getAvailableFrom())
            .availableTo(template.getAvailableTo())
            .createdAt(template.getCreatedAt())
            .updatedAt(template.getUpdatedAt())
            .build();
    }
}
