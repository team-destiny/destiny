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

    private Boolean isDuplicateUsable;

    private Integer maxDiscountAmount;
    private Integer dailyIssueLimit;
    private Integer perUserTotalLimit;

    private LocalDateTime createdAt;
}
