package com.destiny.couponservice.domain.repository;

import com.destiny.couponservice.domain.entity.CouponTemplate;
import com.destiny.couponservice.presentation.dto.request.CouponTemplateSearchRequest;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CouponTemplateRepository {

    CouponTemplate create(CouponTemplate couponTemplate);

    Optional<CouponTemplate> findById(UUID templateId);

    boolean existsByCode(String code);

    Page<CouponTemplate> search(CouponTemplateSearchRequest req, Pageable pageable);

    void delete(CouponTemplate template);

    List<CouponTemplate> findByIdIn(Collection<UUID> ids);

    int decreaseIssueLimit(UUID templateId);

}
