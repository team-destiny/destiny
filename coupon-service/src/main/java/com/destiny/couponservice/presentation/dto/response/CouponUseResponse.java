package com.destiny.couponservice.presentation.dto.response;

import com.destiny.couponservice.domain.enums.DiscountType;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CouponUseResponse {

    private UUID issuedCouponId;
    private UUID orderId;

    private Integer orderAmount;     // 주문 금액
    private Integer discountAmount;  // 실제 할인 금액
    private Integer finalAmount;     // 최종 결제 금액

    private String couponName;
    private DiscountType discountType;
    private Integer discountValue;
    private Integer maxDiscountAmount;
    private Integer minOrderAmount;

    private LocalDateTime usedAt;
}
