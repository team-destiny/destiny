package com.destiny.couponservice.domain.repository;

import com.destiny.couponservice.domain.entity.CouponTemplate;
import java.util.Optional;
import java.util.UUID;

public interface CouponTemplateRepository {

    CouponTemplate create(CouponTemplate couponTemplate);

    Optional<CouponTemplate> findById(UUID templateId);

    boolean existsByCode(String code);
}
