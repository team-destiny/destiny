package com.destiny.couponservice.presentation.dto.request;


import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CouponCancelRequest {

    @NotNull
    private UUID orderId;

}
