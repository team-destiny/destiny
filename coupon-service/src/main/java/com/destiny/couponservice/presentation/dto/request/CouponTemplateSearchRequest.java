package com.destiny.couponservice.presentation.dto.request;

import com.destiny.couponservice.domain.enums.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CouponTemplateSearchRequest {

    private String code;
    private String name;
    private DiscountType discountType;
}
