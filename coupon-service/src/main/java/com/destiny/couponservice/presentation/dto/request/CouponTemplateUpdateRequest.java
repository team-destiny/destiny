package com.destiny.couponservice.presentation.dto.request;

import com.destiny.couponservice.domain.enums.DiscountType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CouponTemplateUpdateRequest {

    private String name;

    private DiscountType discountType;

    private Integer discountValue;

    @PositiveOrZero
    private Integer minOrderAmount;

    private Boolean isDuplicateUsable;

    private Integer maxDiscountAmount;

    private Integer dailyIssueLimit;

    private Integer perUserTotalLimit;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime availableFrom;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime availableTo;

}
