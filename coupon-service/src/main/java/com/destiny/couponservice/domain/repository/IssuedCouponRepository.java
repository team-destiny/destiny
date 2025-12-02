package com.destiny.couponservice.domain.repository;

import com.destiny.couponservice.domain.entity.IssuedCoupon;
import com.destiny.couponservice.domain.enums.IssuedCouponStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IssuedCouponRepository {

    IssuedCoupon save(IssuedCoupon issuedCoupon);

    Optional<IssuedCoupon> findById(UUID id);

    // 내 쿠폰 단건 조회
    Optional<IssuedCoupon> findByIdAndUserId(UUID id, UUID userId);

    // 중복 발급 체크
    boolean existsByUserIdAndCouponTemplateId(UUID userId, UUID couponTemplateId);

    // 내 쿠폰 목록
    Page<IssuedCoupon> findByUserIdAndStatus(UUID id, IssuedCouponStatus status, Pageable pageable);

}
