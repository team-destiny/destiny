package com.destiny.couponservice.application.service.impl;

import com.destiny.couponservice.application.service.IssuedCouponService;
import com.destiny.couponservice.application.service.exception.IssuedCouponErrorCode;
import com.destiny.couponservice.domain.entity.CouponTemplate;
import com.destiny.couponservice.domain.entity.IssuedCoupon;
import com.destiny.couponservice.domain.enums.DiscountType;
import com.destiny.couponservice.domain.enums.IssuedCouponStatus;
import com.destiny.couponservice.domain.repository.CouponTemplateRepository;
import com.destiny.couponservice.domain.repository.IssuedCouponRepository;
import com.destiny.couponservice.infrastructure.messaging.event.command.CouponRollbackRequestEvent;
import com.destiny.couponservice.infrastructure.messaging.event.command.CouponValidateCommand;
import com.destiny.couponservice.infrastructure.messaging.event.result.CouponValidateFailEvent;
import com.destiny.couponservice.infrastructure.messaging.event.result.CouponValidateSuccessEvent;
import com.destiny.couponservice.infrastructure.messaging.producer.CouponValidateProducer;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class IssuedCouponServiceImpl implements IssuedCouponService {

    private final IssuedCouponRepository issuedCouponRepository;
    private final CouponTemplateRepository couponTemplateRepository;
    private final CouponValidateProducer couponValidateProducer;

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

        IssuedCoupon issuedCoupon = IssuedCoupon.builder().userId(userId)
            .couponTemplateId(template.getId()).status(IssuedCouponStatus.AVAILABLE).issuedAt(now)
            .expiredAt(expiredAt).build();

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
    public IssuedCouponSearchResponse getIssuedCoupons(UUID userId, IssuedCouponStatus status,
        Pageable pageable) {

        Page<IssuedCoupon> page = issuedCouponRepository.findByUserIdAndStatus(userId, status,
            pageable);

        List<UUID> templateIds = page.getContent().stream().map(IssuedCoupon::getCouponTemplateId)
            .distinct().toList();

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

    private int calculateDiscountAmount(int orderAmount, CouponTemplate template) {
        DiscountType type = template.getDiscountType();

        if (type == DiscountType.FIXED) {
            // 정액 할인
            return Math.min(orderAmount, template.getDiscountValue());
        }

        // 정률 할인
        int rawDiscount = (int) Math.floor(orderAmount * (template.getDiscountValue() / 100.0));

        Integer maxDiscount = template.getMaxDiscountAmount();
        if (maxDiscount != null && maxDiscount > 0) {
            return Math.min(rawDiscount, maxDiscount);
        }

        return rawDiscount;

    }


    @Override
    @Transactional
    public void cancelCouponUse(UUID userId, UUID issuedCouponId, UUID orderId) {

        IssuedCoupon issuedCoupon = issuedCouponRepository.findById(issuedCouponId)
            .orElseThrow(() -> new BizException(IssuedCouponErrorCode.ISSUED_COUPON_NOT_FOUND));

        //  현재 사용자 권한 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isMaster = authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_MASTER"));

        // MASTER 아니면 소유자 검증
        if (!isMaster && !issuedCoupon.getUserId().equals(userId)) {
            throw new BizException(IssuedCouponErrorCode.INVALID_OWNER);
        }

        if (issuedCoupon.getStatus() != IssuedCouponStatus.USED) {
            throw new BizException(IssuedCouponErrorCode.INVALID_CANCEL_TARGET);
        }

        if (!orderId.equals(issuedCoupon.getOrderId())) {
            throw new BizException(IssuedCouponErrorCode.INVALID_CANCEL_TARGET);
        }

        issuedCoupon.cancelUse();
    }

    @Override
    @Transactional
    public void handleCouponValidate(CouponValidateCommand command) {

        UUID couponId = command.couponId();

        try {
            IssuedCoupon coupon = issuedCouponRepository.findById(couponId)
                .orElseThrow(() -> new BizException(IssuedCouponErrorCode.ISSUED_COUPON_NOT_FOUND));

            LocalDateTime now = LocalDateTime.now();

            // 1) 상태/만료 검증
            if (!coupon.isUsable(now)) {
                if (now.isAfter(coupon.getExpiredAt())) {
                    // 만료된 경우는 도메인 상태만 EXPIRED로 바꾸고
                    coupon.expire(now);
                    // 예외 던져서 아래 catch 블록에서 fail 이벤트 발행
                    throw new BizException(IssuedCouponErrorCode.COUPON_EXPIRED);
                }
                throw new BizException(IssuedCouponErrorCode.INVALID_COUPON_STATUS);
            }

            // 2) 템플릿 조회
            CouponTemplate template = couponTemplateRepository.findById(
                    coupon.getCouponTemplateId())
                .orElseThrow(() -> new BizException(IssuedCouponErrorCode.TEMPLATE_NOT_FOUND));

            // 3) 최소 주문 금액 검증
            if (command.originalAmount() < template.getMinOrderAmount()) {
                throw new BizException(IssuedCouponErrorCode.MIN_ORDER_AMOUNT_NOT_MET);
            }

            // 4) 할인 계산
            int discountAmount = calculateDiscountAmount(
                command.originalAmount(),
                template
            );
            int finalAmount = Math.max(0, command.originalAmount() - discountAmount);

            // 5) 사용 처리
            coupon.use(command.orderId(), now);

            // 6) 성공 이벤트 발행
            CouponValidateSuccessEvent event = CouponValidateSuccessEvent.builder()
                .orderId(command.orderId())
                .couponId(couponId)
                .finalAmount(finalAmount)
                .build();

            couponValidateProducer.sendSuccess(event);

        } catch (Exception e) {

            log.error("[handleCouponValidate] 쿠폰 검증 처리 중 예외 발생 - orderId={}, couponId={}",
                command.orderId(), couponId, e);

            CouponValidateFailEvent failEvent = CouponValidateFailEvent.builder()
                .orderId(command.orderId())
                .couponId(couponId)
                .errorMessage("쿠폰 사용에 실패하였습니다.")
                .build();

            try {
                couponValidateProducer.sendFail(failEvent);
            } catch (Exception sendEx) {
                log.error(
                    "[handleCouponValidate] coupon-use-fail 전송 실패 - 수동 개입 필요. orderId={}, couponId={}",
                    command.orderId(), couponId, sendEx);
            }
        }
    }


    @Override
    @Transactional
    public void couponRollback(CouponRollbackRequestEvent event) {
        IssuedCoupon coupon = issuedCouponRepository.findById(event.couponId()).orElse(null);

        coupon.updateStatus(IssuedCouponStatus.AVAILABLE, LocalDateTime.now());

    }

}
