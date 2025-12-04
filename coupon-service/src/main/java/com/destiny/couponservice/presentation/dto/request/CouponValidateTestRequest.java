package com.destiny.couponservice.presentation.dto.request;

import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CouponValidateTestRequest {

    private UUID couponId;       // 발급 쿠폰 ID (IssuedCoupon ID)
    private Integer originalAmount; // 주문 금액
}
