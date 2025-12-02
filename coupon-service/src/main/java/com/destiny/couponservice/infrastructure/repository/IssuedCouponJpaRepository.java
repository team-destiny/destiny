package com.destiny.couponservice.infrastructure.repository;

import com.destiny.couponservice.domain.entity.IssuedCoupon;
import com.destiny.couponservice.domain.enums.IssuedCouponStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssuedCouponJpaRepository extends JpaRepository<IssuedCoupon, UUID> {

    Optional<IssuedCoupon> findByIdAndUserId(UUID id, UUID userId);

    boolean existsByUserIdAndCouponTemplateId(UUID userId, UUID couponTemplateId);

    Page<IssuedCoupon> findByUserIdAndStatus(UUID userId, IssuedCouponStatus status,
        Pageable pageable);

}
