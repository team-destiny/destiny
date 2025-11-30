package com.destiny.couponservice.presentation.dto.request;

import com.destiny.couponservice.domain.enums.DiscountType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CouponTemplateUpdateRequest {

    private String name;

    private DiscountType discountType;

    private Integer discountValue;

    private Integer minOrderAmount;

    private Boolean isDuplicateUsable;

    private Integer maxDiscountAmount;

    private Integer dailyIssueLimit;

    private Integer perUserTotalLimit;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime availableFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime availableTo;
}
