package com.destiny.couponservice.application.service.impl;

import com.destiny.couponservice.application.service.IssuedCouponService;
import com.destiny.couponservice.application.service.exception.IssuedCouponErrorCode;
import com.destiny.couponservice.domain.entity.CouponTemplate;
import com.destiny.couponservice.domain.entity.IssuedCoupon;
import com.destiny.couponservice.domain.enums.DiscountType;
import com.destiny.couponservice.domain.enums.IssuedCouponStatus;
import com.destiny.couponservice.domain.repository.CouponTemplateRepository;
import com.destiny.couponservice.domain.repository.IssuedCouponRepository;
import com.destiny.couponservice.infrastructure.messaging.event.command.CouponCancelFailEvent;
import com.destiny.couponservice.infrastructure.messaging.event.command.CouponCancelRequestEvent;
import com.destiny.couponservice.infrastructure.messaging.event.command.CouponCancelSuccessEvent;
import com.destiny.couponservice.infrastructure.messaging.event.command.CouponRollbackRequestEvent;
import com.destiny.couponservice.infrastructure.messaging.event.command.CouponValidateCommand;
import com.destiny.couponservice.infrastructure.messaging.event.result.CouponValidateFailEvent;
import com.destiny.couponservice.infrastructure.messaging.event.result.CouponValidateSuccessEvent;
import com.destiny.couponservice.infrastructure.messaging.producer.CouponCancelProducer;
import com.destiny.couponservice.infrastructure.messaging.producer.CouponValidateProducer;
import com.destiny.couponservice.presentation.dto.response.IssuedCouponDetailResponse;
import com.destiny.couponservice.presentation.dto.response.IssuedCouponListItemResponse;
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
    private final CouponCancelProducer couponCancelProducer;

    private static final int DEFAULT_EXPIRE_DAYS_AFTER_ISSUE = 7;


    // 쿠폰 발급
    @Override
    @Transactional
    public IssuedCouponDetailResponse issueCoupon(UUID userId, UUID couponTemplateId) {

        // 1) 템플릿 조회
        CouponTemplate template = couponTemplateRepository.findById(couponTemplateId)
            .orElseThrow(() -> new BizException(IssuedCouponErrorCode.TEMPLATE_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();

        // 2) 발급 가능 기간 검증
        if (now.isBefore(template.getAvailableFrom()) || now.isAfter(template.getAvailableTo())) {
            throw new BizException(IssuedCouponErrorCode.ISSUE_PERIOD_INVALID);
        }

        // 3) 중복 발급 방지
        if (issuedCouponRepository.existsByUserIdAndCouponTemplateId(userId, couponTemplateId)) {
            throw new BizException(IssuedCouponErrorCode.ALREADY_ISSUED);
        }

        // 4) issueLimit 감소
        if (template.getIssueLimit() != null) {
            int updated = couponTemplateRepository.decreaseIssueLimit(couponTemplateId);
            if (updated == 0) {
                throw new BizException(IssuedCouponErrorCode.ISSUE_LIMIT_EXHAUSTED);
            }
        }

        // 5) 만료 시각 계산 (발급일로부터 7일 동안 사용가능)
        LocalDateTime expiredAt = now.plusDays(DEFAULT_EXPIRE_DAYS_AFTER_ISSUE);

        // 6) 발급 쿠폰 생성
        IssuedCoupon issuedCoupon = IssuedCoupon.builder().userId(userId)
            .couponTemplateId(template.getId()).status(IssuedCouponStatus.AVAILABLE).issuedAt(now)
            .expiredAt(expiredAt).build();

        try {
            IssuedCoupon saved = issuedCouponRepository.save(issuedCoupon);
            return IssuedCouponDetailResponse.from(saved, template);

        } catch (DataIntegrityViolationException e) {
            throw new BizException(IssuedCouponErrorCode.ALREADY_ISSUED);
        }
    }


    // 내가 발급받은 쿠폰 단건 조회
    @Override
    public IssuedCouponDetailResponse getIssuedCoupon(UUID userId, UUID issuedCouponId) {
        IssuedCoupon issuedCoupon = issuedCouponRepository.findById(issuedCouponId)
            .orElseThrow(() -> new BizException(IssuedCouponErrorCode.ISSUED_COUPON_NOT_FOUND));

        // 본인 소유 쿠폰인지 검증
        if (!issuedCoupon.getUserId().equals(userId)) {
            throw new BizException(IssuedCouponErrorCode.INVALID_OWNER);
        }

        CouponTemplate template = couponTemplateRepository.findById(
                issuedCoupon.getCouponTemplateId())
            .orElseThrow(() -> new BizException(IssuedCouponErrorCode.TEMPLATE_NOT_FOUND));

        return IssuedCouponDetailResponse.from(issuedCoupon, template);
    }

    // 내가 받은 쿠폰 목록 조회
    @Override
    @Transactional(readOnly = true)
    public IssuedCouponSearchResponse getIssuedCoupons(UUID userId, IssuedCouponStatus status,
        Pageable pageable) {

        Page<IssuedCoupon> page = issuedCouponRepository.findByUserIdAndStatus(userId, status,
            pageable);

        List<UUID> templateIds = page.getContent().stream().map(IssuedCoupon::getCouponTemplateId)
            .distinct().toList();

        List<CouponTemplate> templates = couponTemplateRepository.findByIdIn(templateIds);

        Map<UUID, CouponTemplate> templateMap = templates.stream()
            .collect(Collectors.toMap(CouponTemplate::getId, Function.identity()));

        Page<IssuedCouponListItemResponse> dtoPage = page.map(issued -> {
            CouponTemplate template = templateMap.get(issued.getCouponTemplateId());
            if (template == null) {
                throw new BizException(IssuedCouponErrorCode.TEMPLATE_NOT_FOUND);
            }
            return IssuedCouponListItemResponse.from(issued, template);
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

            //  1) 이미 사용된 쿠폰이면 명확한 에러로 분기
            if (coupon.getStatus() == IssuedCouponStatus.USED) {
                throw new BizException(IssuedCouponErrorCode.ALREADY_USED_COUPON);
            }

            // 2) 만료된 쿠폰이면 상태 업데이트 후 만료 예외
            if (now.isAfter(coupon.getExpiredAt())) {
                coupon.expire(now);
                throw new BizException(IssuedCouponErrorCode.COUPON_EXPIRED);
            }

            //  3) 그 외 사용 불가 상태 (AVAILABLE이 아니면 사용 불가)
            if (coupon.getStatus() != IssuedCouponStatus.AVAILABLE) {
                throw new BizException(IssuedCouponErrorCode.INVALID_COUPON_STATUS);
            }

            // 4) 템플릿 조회
            CouponTemplate template = couponTemplateRepository.findById(
                    coupon.getCouponTemplateId())
                .orElseThrow(() -> new BizException(IssuedCouponErrorCode.TEMPLATE_NOT_FOUND));

            // 5) 최소 주문 금액 검증
            if (command.originalAmount() < template.getMinOrderAmount()) {
                throw new BizException(IssuedCouponErrorCode.MIN_ORDER_AMOUNT_NOT_MET);
            }

            // 6) 할인 계산
            int discountAmount = calculateDiscountAmount(command.originalAmount(), template);
            int finalAmount = Math.max(0, command.originalAmount() - discountAmount);

            // 7) 사용 처리
            coupon.use(command.orderId(), now);

            // 8) 성공 이벤트 발행
            CouponValidateSuccessEvent event = CouponValidateSuccessEvent.builder()
                .orderId(command.orderId())
                .couponId(couponId)
                .finalAmount(finalAmount)
                .build();

            couponValidateProducer.sendSuccess(event);

        } catch (Exception e) {

            log.error("[handleCouponValidate] 쿠폰 검증 처리 중 예외 발생 - orderId={}, couponId={}",
                command.orderId(), couponId, e);

            // ✅ BizException이면 그 메시지를 그대로 사가에 전달 (원하는 failReason)
            String errorMessage = "쿠폰 사용에 실패하였습니다.";
            if (e instanceof BizException biz && biz.getMessage() != null && !biz.getMessage()
                .isBlank()) {
                errorMessage = biz.getMessage();
            }

            CouponValidateFailEvent failEvent = CouponValidateFailEvent.builder()
                .orderId(command.orderId())
                .couponId(couponId)
                .errorMessage(errorMessage)
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

        if (coupon == null) {
            log.warn("[couponRollback] coupon not found - couponId={}", event.couponId());
            return;
        }

        coupon.updateStatus(IssuedCouponStatus.AVAILABLE, LocalDateTime.now());
    }

    @Override
    @Transactional
    public void couponCancel(CouponCancelRequestEvent event) {
        UUID sagaId = event.sagaId();
        UUID couponId = event.couponId();

        try {
            IssuedCoupon coupon = issuedCouponRepository.findById(couponId)
                .orElseThrow(() -> new BizException(IssuedCouponErrorCode.ISSUED_COUPON_NOT_FOUND));

            if (coupon.getStatus() == IssuedCouponStatus.AVAILABLE) {
                log.info("[couponCancel] already available - sagaId={}, couponId={}", sagaId,
                    couponId);
                couponCancelProducer.sendSuccess(new CouponCancelSuccessEvent(sagaId));
                return;
            }

            if (coupon.getStatus() == IssuedCouponStatus.USED) {
                coupon.cancelUse();
                couponCancelProducer.sendSuccess(new CouponCancelSuccessEvent(sagaId));

                log.info("[couponCancel] success - sagaId={}, couponId={}", sagaId, couponId);
                return;
            }

            sendCancelFail(sagaId, "취소할 수 없는 쿠폰 상태입니다.");

            log.warn("[couponCancel] fail - invalid status. sagaId={}, couponId={}, status={}",
                sagaId, couponId, coupon.getStatus());

        } catch (BizException e) {
            sendCancelFail(sagaId, e.getMessage());
        } catch (Exception e) {
            log.error("[couponCancel] system error - sagaId={}, couponId={}", sagaId, couponId, e);
            sendCancelFail(sagaId, "쿠폰 사용 취소 처리 중 시스템 오류가 발생했습니다.");
        }
    }

    private void sendCancelFail(UUID sagaId, String message) {
        try {
            couponCancelProducer.sendFail(new CouponCancelFailEvent(sagaId, message));
        } catch (Exception sendEx) {
            log.error("[couponCancel] coupon-cancel-fail 전송 실패 - 수동 개입 필요. sagaId={}", sagaId,
                sendEx);
        }
    }
}

