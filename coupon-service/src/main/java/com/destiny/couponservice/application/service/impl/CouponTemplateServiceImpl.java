package com.destiny.couponservice.application.service.impl;

import com.destiny.couponservice.application.service.CouponTemplateService;
import com.destiny.couponservice.application.service.exception.CouponErrorCode;
import com.destiny.couponservice.domain.entity.CouponTemplate;
import com.destiny.couponservice.domain.enums.DiscountType;
import com.destiny.couponservice.domain.repository.CouponTemplateRepository;
import com.destiny.couponservice.presentation.dto.request.CouponTemplateCreateRequest;
import com.destiny.couponservice.presentation.dto.request.CouponTemplateSearchRequest;
import com.destiny.couponservice.presentation.dto.request.CouponTemplateUpdateRequest;
import com.destiny.couponservice.presentation.dto.response.CouponTemplateCreateResponse;
import com.destiny.couponservice.presentation.dto.response.CouponTemplateGetResponse;
import com.destiny.global.exception.BizException;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponTemplateServiceImpl implements CouponTemplateService {

    private final CouponTemplateRepository couponTemplateRepository;


    //쿠폰 템플릿 생성
    @Override
    @Transactional
    public CouponTemplateCreateResponse create(CouponTemplateCreateRequest req) {

        // 중복 코드 검증
        if (couponTemplateRepository.existsByCode(req.getCode())) {
            throw new BizException(CouponErrorCode.DUPLICATE_TEMPLATE_CODE);
        }

        //  날짜 범위 검증
        if (req.getAvailableTo().isBefore(req.getAvailableFrom())) {
            throw new BizException(CouponErrorCode.INVALID_DATE_RANGE);
        }

        // 할인 값 검증
        if (req.getDiscountType() == DiscountType.RATE) {
            // 정률 할인: 1~100%
            if (req.getDiscountValue() < 1 || req.getDiscountValue() > 100) {
                throw new BizException(CouponErrorCode.INVALID_DISCOUNT_VALUE);
            }

            // 정률 할인일 때 최대 할인 금액 필수
            if (req.getMaxDiscountAmount() == null) {
                throw new BizException(CouponErrorCode.MISSING_MAX_DISCOUNT);
            }
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
            .createdAt(saved.getCreatedAt())
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

    // 쿠폰템플릿 목록 조회
    @Override
    @Transactional
    public Page<CouponTemplateGetResponse> search(CouponTemplateSearchRequest req,
        Pageable pageable) {

        return couponTemplateRepository.search(req, pageable)
            .map(CouponTemplateGetResponse::from);
    }


    //쿠폰 템플릿 수정
    @Override
    @Transactional
    public CouponTemplateGetResponse update(UUID templateId, CouponTemplateUpdateRequest req) {

        CouponTemplate template = couponTemplateRepository.findById(templateId)
            .orElseThrow(() -> new BizException(CouponErrorCode.TEMPLATE_NOT_FOUND));

        //  패치 후 기준 값 계산 (null 은 기존 값 유지)
        DiscountType newType =
            req.getDiscountType() != null ? req.getDiscountType() : template.getDiscountType();
        Integer newDiscountValue =
            req.getDiscountValue() != null ? req.getDiscountValue() : template.getDiscountValue();

        LocalDateTime newFrom =
            req.getAvailableFrom() != null ? req.getAvailableFrom() : template.getAvailableFrom();
        LocalDateTime newTo =
            req.getAvailableTo() != null ? req.getAvailableTo() : template.getAvailableTo();

        Integer newMaxDiscountAmount =
            req.getMaxDiscountAmount() != null
                ? req.getMaxDiscountAmount()
                : template.getMaxDiscountAmount();

        if (newTo.isBefore(newFrom)) {
            throw new BizException(CouponErrorCode.INVALID_DATE_RANGE);
        }

        if (newType == DiscountType.RATE) {
            if (newDiscountValue == null
                || newDiscountValue < 1
                || newDiscountValue > 100) {
                throw new BizException(CouponErrorCode.INVALID_DISCOUNT_VALUE);
            }
            if (newMaxDiscountAmount == null) {
                throw new BizException(CouponErrorCode.MISSING_MAX_DISCOUNT);
            }
        }

        template.update(
            req.getName(),
            req.getDiscountType(),
            req.getDiscountValue(),
            req.getMinOrderAmount(),
            req.getIsDuplicateUsable(),
            req.getMaxDiscountAmount(),
            req.getDailyIssueLimit(),
            req.getPerUserTotalLimit(),
            req.getAvailableFrom(),
            req.getAvailableTo()
        );

        return CouponTemplateGetResponse.from(template);
    }
}

