package com.destiny.couponservice.infrastructure.repository;

import com.destiny.couponservice.domain.entity.CouponTemplate;
import com.destiny.couponservice.domain.repository.CouponTemplateRepository;
import com.destiny.couponservice.infrastructure.repository.spec.CouponTemplateSpecification;
import com.destiny.couponservice.presentation.dto.request.CouponTemplateSearchRequest;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    @Override
    public Page<CouponTemplate> search(CouponTemplateSearchRequest req, Pageable pageable) {
        Specification<CouponTemplate> spec = Specification.allOf(
            CouponTemplateSpecification.codeContains(req.getCode()),
            CouponTemplateSpecification.nameContains(req.getName()),
            CouponTemplateSpecification.typeEquals(req.getDiscountType()));

        return couponTemplateJpaRepository.findAll(spec, pageable);
    }

    @Override
    public void delete(CouponTemplate template) {
        couponTemplateJpaRepository.delete(template);
    }

    @Override
    public List<CouponTemplate> findByIdIn(Collection<UUID> ids) {
        return couponTemplateJpaRepository.findByIdIn(ids);
    }

    @Override
    public int decreaseIssueLimit(UUID templateId) {
        return couponTemplateJpaRepository.decreaseIssueLimit(templateId);
    }

}
