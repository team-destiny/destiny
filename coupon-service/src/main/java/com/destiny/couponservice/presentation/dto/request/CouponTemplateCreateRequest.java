package com.destiny.couponservice.presentation.dto.request;


import com.destiny.couponservice.domain.enums.DiscountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CouponTemplateCreateRequest {

    @NotBlank
    private String code;  // 쿠폰 코드 (UNIQUE)

    @NotBlank
    private String name;  // 쿠폰 이름

    @NotNull
    private DiscountType discountType; // FIXED or RATE

    @NotNull
    @Positive
    private Integer discountValue; // 할인 금액 또는 비율

    @NotNull
    @PositiveOrZero
    private Integer minOrderAmount; // 최소 주문 금액 (기본 0)

    @NotNull
    private LocalDateTime availableFrom; // 발급 시작

    @NotNull
    private LocalDateTime availableTo;   // 발급 종료

    private Boolean isDuplicateUsable;   // 중복 사용 가능 여부

    private Integer maxDiscountAmount;   // 정률 할인 시 최대 금액

    private Integer dailyIssueLimit;     // 하루 발급 제한

    private Integer perUserTotalLimit;   // 사용자별 발급 제한
}
