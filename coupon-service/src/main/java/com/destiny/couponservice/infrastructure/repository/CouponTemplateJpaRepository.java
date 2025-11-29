package com.destiny.couponservice.infrastructure.repository;

import com.destiny.couponservice.domain.entity.CouponTemplate;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponTemplateJpaRepository extends JpaRepository<CouponTemplate, UUID> {

    boolean existsByCode(String code);
}
