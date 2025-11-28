package com.destiny.couponservice.infrastructure.repository;

import com.destiny.couponservice.domain.entity.CouponTemplate;
import com.destiny.couponservice.domain.repository.CouponTemplateRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CouponTemplateRepositoryImpl implements CouponTemplateRepository {

    private final CouponTemplateJpaRepository couponTemplateJpaRepository;

    @Override
    public CouponTemplate create(CouponTemplate couponTemplate) {
        return couponTemplateJpaRepository.save(couponTemplate);
    }

    @Override
    public Optional<CouponTemplate> findById(UUID templateId) {
        return couponTemplateJpaRepository.findById(templateId);
    }

    @Override
    public boolean existsByCode(String code) {
        return couponTemplateJpaRepository.existsByCode(code);
    }
}
