package com.destiny.couponservice.application.service.impl;

import com.destiny.couponservice.application.service.CouponTemplateService;
import com.destiny.couponservice.application.service.exception.CouponErrorCode;
import com.destiny.couponservice.domain.entity.CouponTemplate;
import com.destiny.couponservice.domain.repository.CouponTemplateRepository;
import com.destiny.couponservice.presentation.dto.request.CouponTemplateCreateRequest;
import com.destiny.couponservice.presentation.dto.response.CouponTemplateCreateResponse;
import com.destiny.couponservice.presentation.dto.response.CouponTemplateGetResponse;
import com.destiny.global.exception.BizException;
import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponTemplateServiceImpl implements CouponTemplateService {

    private final CouponTemplateRepository couponTemplateRepository;


    //쿠폰 템플릿 생성
    @Override
    @Transactional
    public CouponTemplateCreateResponse create(CouponTemplateCreateRequest req) {

        if (couponTemplateRepository.existsByCode(req.getCode())) {
            throw new BizException(CouponErrorCode.DUPLICATE_TEMPLATE_CODE);
        }

        CouponTemplate couponTemplate = CouponTemplate.builder()
            .code(req.getCode())
            .name(req.getName())
            .discountType(req.getDiscountType())
            .discountValue(req.getDiscountValue())
            .minOrderAmount(req.getMinOrderAmount())
            .isDuplicateUsable(
                req.getIsDuplicateUsable() != null ? req.getIsDuplicateUsable() : false
            )
            .maxDiscountAmount(req.getMaxDiscountAmount())
            .dailyIssueLimit(req.getDailyIssueLimit())
            .perUserTotalLimit(req.getPerUserTotalLimit())
            .availableFrom(req.getAvailableFrom())
            .availableTo(req.getAvailableTo())
            .build();

        CouponTemplate saved = couponTemplateRepository.create(couponTemplate);

        return CouponTemplateCreateResponse.builder()
            .id(saved.getId())
            .code(saved.getCode())
            .name(saved.getName())
            .discountType(saved.getDiscountType())
            .discountValue(saved.getDiscountValue())
            .minOrderAmount(saved.getMinOrderAmount())
            .availableFrom(saved.getAvailableFrom())
            .availableTo(saved.getAvailableTo())
            .isDuplicateUsable(saved.getIsDuplicateUsable())
            .maxDiscountAmount(saved.getMaxDiscountAmount())
            .dailyIssueLimit(saved.getDailyIssueLimit())
            .perUserTotalLimit(saved.getPerUserTotalLimit())
            .createdAt(saved.getCreatedAt())   // Auditing 값
            .build();
    }


    // 쿠폰템플릿 단건조회
    @Override
    @Transactional
    public CouponTemplateGetResponse getTemplate(UUID templateId) {

        CouponTemplate template = couponTemplateRepository.findById(templateId)
            .orElseThrow(() -> new BizException(CouponErrorCode.TEMPLATE_NOT_FOUND));

        return CouponTemplateGetResponse.builder()
            .id(template.getId())
            .code(template.getCode())
            .name(template.getName())
            .discountType(template.getDiscountType())
            .discountValue(template.getDiscountValue())
            .minOrderAmount(template.getMinOrderAmount())
            .isDuplicateUsable(template.getIsDuplicateUsable())
            .maxDiscountAmount(template.getMaxDiscountAmount())
            .dailyIssueLimit(template.getDailyIssueLimit())
            .perUserTotalLimit(template.getPerUserTotalLimit())
            .availableFrom(template.getAvailableFrom())
            .availableTo(template.getAvailableTo())
            .createdAt(template.getCreatedAt())
            .updatedAt(template.getUpdatedAt())
            .build();
    }
}
