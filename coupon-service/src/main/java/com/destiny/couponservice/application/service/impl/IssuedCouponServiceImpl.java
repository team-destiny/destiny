package com.destiny.couponservice.application.service.impl;

import com.destiny.couponservice.application.service.IssuedCouponService;
import com.destiny.couponservice.application.service.exception.IssuedCouponErrorCode;
import com.destiny.couponservice.domain.entity.CouponTemplate;
import com.destiny.couponservice.domain.entity.IssuedCoupon;
import com.destiny.couponservice.domain.enums.IssuedCouponStatus;
import com.destiny.couponservice.domain.repository.CouponTemplateRepository;
import com.destiny.couponservice.domain.repository.IssuedCouponRepository;
import com.destiny.couponservice.presentation.dto.response.IssuedCouponResponseDto;
import com.destiny.couponservice.presentation.dto.response.IssuedCouponSearchResponse;
import com.destiny.global.exception.BizException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IssuedCouponServiceImpl implements IssuedCouponService {

    private final IssuedCouponRepository issuedCouponRepository;
    private final CouponTemplateRepository couponTemplateRepository;

    /**
     * 쿠폰 발급
     */
    @Override
    @Transactional
    public IssuedCouponResponseDto issueCoupon(UUID userId, UUID couponTemplateId) {
        //  템플릿 조회
        CouponTemplate template = couponTemplateRepository.findById(couponTemplateId)
            .orElseThrow(() -> new BizException(IssuedCouponErrorCode.TEMPLATE_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();

        //   발급 가능 기간 검증
        if (now.isBefore(template.getAvailableFrom()) || now.isAfter(template.getAvailableTo())) {
            throw new BizException(IssuedCouponErrorCode.ISSUE_PERIOD_INVALID);
        }

        //  중복 발급 방지
        if (Boolean.FALSE.equals(template.getIsDuplicateUsable())
            && issuedCouponRepository.existsByUserIdAndCouponTemplateId(userId, couponTemplateId)) {
            throw new BizException(IssuedCouponErrorCode.ALREADY_ISSUED);
        }

        // 만료 시각 계산 (현재 쿠폰템플릿의 availableTo 를 만료 시각으로 사용)
        // TODO  "발급 후 N일" 등의 정책 추가 후 수정예정
        LocalDateTime expiredAt = template.getAvailableTo();

        IssuedCoupon issuedCoupon = IssuedCoupon.builder()
            .userId(userId)
            .couponTemplateId(template.getId())
            .status(IssuedCouponStatus.AVAILABLE)
            .issuedAt(now)
            .expiredAt(expiredAt)
            .build();

        try {
            IssuedCoupon saved = issuedCouponRepository.save(issuedCoupon);
            return IssuedCouponResponseDto.from(saved, template);
        } catch (DataIntegrityViolationException e) {
            throw new BizException(IssuedCouponErrorCode.ALREADY_ISSUED);
        }

    }

    // 내가 발급받은 쿠폰 단건 조회
    @Override
    public IssuedCouponResponseDto getIssuedCoupon(UUID userId, UUID issuedCouponId) {
        IssuedCoupon issuedCoupon = issuedCouponRepository.findById(issuedCouponId)
            .orElseThrow(() -> new BizException(IssuedCouponErrorCode.ISSUED_COUPON_NOT_FOUND));

        // 본인 소유 쿠폰인지 검증
        if (!issuedCoupon.getUserId().equals(userId)) {
            throw new BizException(IssuedCouponErrorCode.INVALID_OWNER);
        }

        CouponTemplate template = couponTemplateRepository.findById(
                issuedCoupon.getCouponTemplateId())
            .orElseThrow(() -> new BizException(IssuedCouponErrorCode.TEMPLATE_NOT_FOUND));

        return IssuedCouponResponseDto.from(issuedCoupon, template);
    }

    //내가 발급받은 쿠폰 목록 조회
    @Override
    public IssuedCouponSearchResponse getIssuedCoupons(UUID userId,
        IssuedCouponStatus status,
        Pageable pageable) {

        Page<IssuedCoupon> page = issuedCouponRepository.findByUserIdAndStatus(userId, status,
            pageable);

        List<UUID> templateIds = page.getContent().stream()
            .map(IssuedCoupon::getCouponTemplateId)
            .distinct()
            .toList();

        List<CouponTemplate> templates = couponTemplateRepository.findByIdIn(templateIds);

        Map<UUID, CouponTemplate> templateMap = templates.stream()
            .collect(Collectors.toMap(CouponTemplate::getId, Function.identity()));

        Page<IssuedCouponResponseDto> dtoPage = page.map(issued -> {
            CouponTemplate template = templateMap.get(issued.getCouponTemplateId());
            if (template == null) {
                throw new BizException(IssuedCouponErrorCode.TEMPLATE_NOT_FOUND);
            }
            return IssuedCouponResponseDto.from(issued, template);
        });

        return IssuedCouponSearchResponse.from(dtoPage);
    }
}
