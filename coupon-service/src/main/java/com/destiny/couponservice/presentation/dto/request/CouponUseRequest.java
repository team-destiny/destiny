package com.destiny.couponservice.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponUseRequest {

    @NotNull
    private UUID orderId;

    @NotNull
    @Positive
    private Integer orderAmount; // 주문 금액(총액)
}
