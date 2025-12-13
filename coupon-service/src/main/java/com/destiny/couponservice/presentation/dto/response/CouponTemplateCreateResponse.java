package com.destiny.couponservice.presentation.dto.response;

import com.destiny.couponservice.domain.enums.DiscountType;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CouponTemplateCreateResponse {

    private UUID id;

    private String code;

    private String name;

    private DiscountType discountType;

    private Integer discountValue;

    private Integer minOrderAmount;

    private LocalDateTime availableFrom;

    private LocalDateTime availableTo;

    private Integer maxDiscountAmount;

    private Integer issueLimit;

    private LocalDateTime createdAt;
}
