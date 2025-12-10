package com.destiny.couponservice.presentation.dto.request;

import com.destiny.couponservice.domain.enums.DiscountType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CouponTemplateUpdateRequest {

    @NotBlank
    private String name;

    @NotNull
    private DiscountType discountType;

    @NotNull
    private Integer discountValue;

    @NotNull
    @PositiveOrZero
    private Integer minOrderAmount;

    @NotNull
    private Boolean isDuplicateUsable;

    @NotNull
    private Integer maxDiscountAmount;

    @NotNull
    private Integer dailyIssueLimit;

    @NotNull
    private Integer perUserTotalLimit;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime availableFrom;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime availableTo;
}
