package com.destiny.couponservice.infrastructure.repository;

import com.destiny.couponservice.domain.entity.CouponTemplate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CouponTemplateJpaRepository extends JpaRepository<CouponTemplate, UUID>,
    JpaSpecificationExecutor<CouponTemplate> {

    boolean existsByCode(String code);

    List<CouponTemplate> findByIdIn(Collection<UUID> ids);
}

