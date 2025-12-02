package com.destiny.couponservice.infrastructure.repository;

import com.destiny.couponservice.domain.entity.IssuedCoupon;
import com.destiny.couponservice.domain.enums.IssuedCouponStatus;
import com.destiny.couponservice.domain.repository.IssuedCouponRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class IssuedCouponRepositoryImpl implements IssuedCouponRepository {

    private final IssuedCouponJpaRepository issuedCouponJpaRepository;

    @Override
    public IssuedCoupon save(IssuedCoupon issuedCoupon) {
        return issuedCouponJpaRepository.save(issuedCoupon);
    }

    @Override
    public Optional<IssuedCoupon> findById(UUID id) {
        return issuedCouponJpaRepository.findById(id);
    }

    @Override
    public Optional<IssuedCoupon> findByIdAndUserId(UUID id, UUID userId) {
        return issuedCouponJpaRepository.findByIdAndUserId(id, userId);
    }

    @Override
    public boolean existsByUserIdAndCouponTemplateId(UUID userId, UUID couponTemplateId) {
        return issuedCouponJpaRepository.existsByUserIdAndCouponTemplateId(userId,
            couponTemplateId);
    }

    @Override
    public Page<IssuedCoupon> findByUserIdAndStatus(UUID userId, IssuedCouponStatus status,
        Pageable pageable) {
        return issuedCouponJpaRepository.findByUserIdAndStatus(userId, status, pageable);
    }
}
