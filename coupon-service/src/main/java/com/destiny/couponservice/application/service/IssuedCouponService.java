package com.destiny.couponservice.application.service;

import com.destiny.couponservice.domain.enums.IssuedCouponStatus;
import com.destiny.couponservice.infrastructure.messaging.event.command.CouponRollbackRequestEvent;
import com.destiny.couponservice.infrastructure.messaging.event.command.CouponValidateCommand;
import com.destiny.couponservice.presentation.dto.response.IssuedCouponDetailResponse;
import com.destiny.couponservice.presentation.dto.response.IssuedCouponSearchResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface IssuedCouponService {

    IssuedCouponDetailResponse issueCoupon(UUID userId, UUID couponTemplateId);

    IssuedCouponDetailResponse getIssuedCoupon(UUID userId, UUID issuedCouponId);

    IssuedCouponSearchResponse getIssuedCoupons(UUID userId, IssuedCouponStatus status,
        Pageable pageable);

    void cancelCouponUse(UUID userId, UUID issuedCouponId, UUID orderId);

    void handleCouponValidate(CouponValidateCommand command);

    void couponRollback(CouponRollbackRequestEvent event);
}
