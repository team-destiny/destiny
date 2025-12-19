package com.destiny.couponservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.destiny.couponservice.application.service.exception.IssuedCouponErrorCode;
import com.destiny.couponservice.application.service.impl.IssuedCouponServiceImpl;
import com.destiny.couponservice.domain.entity.CouponTemplate;
import com.destiny.couponservice.domain.entity.IssuedCoupon;
import com.destiny.couponservice.domain.enums.DiscountType;
import com.destiny.couponservice.domain.enums.IssuedCouponStatus;
import com.destiny.couponservice.domain.repository.CouponTemplateRepository;
import com.destiny.couponservice.domain.repository.IssuedCouponRepository;
import com.destiny.couponservice.presentation.dto.response.IssuedCouponDetailResponse;
import com.destiny.global.exception.BizException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IssuedCouponTest {

    @InjectMocks
    private IssuedCouponServiceImpl issuedCouponService;

    @Mock
    private IssuedCouponRepository issuedCouponRepository;

    @Mock
    private CouponTemplateRepository couponTemplateRepository;

    // 쿠폰 발급 테스트
    /*
        사용자는 하나의 쿠폰템플릿을 1번만 발급받을수있다.
        쿠폰 템플릿을 발급하면 쿠폰템플릿의 수량이 1감소한다
     */
    @Test
    @DisplayName("쿠폰 발급 성공")
    void issueCoupon_success() {
        // given
        UUID userId = UUID.randomUUID();
        UUID templateId = UUID.randomUUID();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.minusDays(1);
        LocalDateTime to = now.plusDays(7);

        CouponTemplate template = CouponTemplate.builder()
            .id(templateId)
            .code("BLACK FRIDAY2025")
            .name("블랙 프라이데이 3000원 할인 쿠폰")
            .discountType(DiscountType.FIXED)
            .discountValue(3000)
            .minOrderAmount(10000)
            .maxDiscountAmount(null)
            .issueLimit(100)
            .availableFrom(from)
            .availableTo(to)
            .build();

        given(couponTemplateRepository.findById(templateId)).willReturn(Optional.of(template));
        given(issuedCouponRepository.existsByUserIdAndCouponTemplateId(userId,
            templateId)).willReturn(false);
        given(couponTemplateRepository.decreaseIssueLimit(templateId)).willReturn(1);

        IssuedCoupon saved = IssuedCoupon.builder()
            .id(UUID.randomUUID())
            .userId(userId)
            .couponTemplateId(templateId)
            .status(IssuedCouponStatus.AVAILABLE)
            .issuedAt(now)
            .expiredAt(now.plusDays(7))
            .build();

        given(issuedCouponRepository.save(any(IssuedCoupon.class))).willReturn(saved);

        // when
        IssuedCouponDetailResponse response = issuedCouponService.issueCoupon(userId, templateId);

        // then
        ArgumentCaptor<IssuedCoupon> captor = ArgumentCaptor.forClass(IssuedCoupon.class);
        verify(issuedCouponRepository).save(captor.capture());
        IssuedCoupon passed = captor.getValue();

        assertThat(passed.getUserId()).isEqualTo(userId);
        assertThat(passed.getCouponTemplateId()).isEqualTo(templateId);
        assertThat(passed.getStatus()).isEqualTo(IssuedCouponStatus.AVAILABLE);

        long days = Duration.between(passed.getIssuedAt(), passed.getExpiredAt()).toDays();
        assertThat(days).isEqualTo(7);

        // issueLimit 감소 호출됐는지 검증
        verify(couponTemplateRepository).decreaseIssueLimit(templateId);

        //  응답 DTO 검증
        assertThat(response.getId()).isEqualTo(saved.getId());
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getCouponTemplateId()).isEqualTo(templateId);
        assertThat(response.getStatus()).isEqualTo(IssuedCouponStatus.AVAILABLE);
        assertThat(response.getCode()).isEqualTo("BLACK FRIDAY2025");
        assertThat(response.getName()).isEqualTo("블랙 프라이데이 3000원 할인 쿠폰");
    }

    // 발급 기간 아닌 쿠폰 발급
    /*
        쿠폰 발급 기간이 아닐때 쿠폰을 발급하면 ISSUE_PERIOD_INVALID 던지는지 검증
     */
    @Test
    @DisplayName("쿠폰 발급 실패 - 쿠폰 발급 기간아님")
    void issueCoupon_invalidPeriod_throwsException() {

        // given
        UUID userId = UUID.randomUUID();
        UUID templateId = UUID.randomUUID();

        // 아직 발급 시작 전인 쿠폰 템플릿
        CouponTemplate template = CouponTemplate.builder()
            .id(templateId)
            .availableFrom(LocalDateTime.now().plusDays(1))
            .availableTo(LocalDateTime.now().plusDays(10))
            .build();

        given(couponTemplateRepository.findById(templateId))
            .willReturn(Optional.of(template));

        // when
        BizException ex = assertThrows(
            BizException.class,
            () -> issuedCouponService.issueCoupon(userId, templateId)
        );

        // then
        // 발급 기간 오류인지 검증
        assertThat(ex.getResponseCode()).
            isEqualTo(IssuedCouponErrorCode.ISSUE_PERIOD_INVALID);

        // 발급 실패 시, 저장 로직 호출 x
        verify(issuedCouponRepository, never())
            .save(any(IssuedCoupon.class));

        // 발급 실패 시, 수량 감소 로직 호출 x
        verify(couponTemplateRepository, never())
            .decreaseIssueLimit(any());

    }

    // 이미 발급된 쿠폰 발급
    /*
        사용자는 하나의 쿠폰템플릿에 한개만 발급가능
        이미 발급 받은 쿠폰을 발급하려고하면 ALREADY_ISSUED 에러발생
     */
    @Test
    @DisplayName("쿠폰 발급 실패 - 이미 발급된 쿠폰")
    void issueCoupon_alreadyIssued_throwsException() {

        // given
        UUID userId = UUID.randomUUID();
        UUID templateId = UUID.randomUUID();

        // 발급 가능한 기간의 쿠폰 템플릿
        CouponTemplate template = CouponTemplate.builder()
            .id(templateId)
            .availableFrom(LocalDateTime.now().minusDays(1))
            .availableTo(LocalDateTime.now().plusDays(7))
            .build();

        given(couponTemplateRepository.findById(templateId))
            .willReturn(Optional.of(template));

        // 해당 유저에게 발급된 쿠폰이 존재
        given(issuedCouponRepository.existsByUserIdAndCouponTemplateId(
            userId, templateId)).willReturn(true);

        // when
        BizException ex = assertThrows(
            BizException.class,
            () -> issuedCouponService.issueCoupon(userId, templateId)
        );

        // then
        // 중복 발급 에러 코드인지 검증 (ALREADY_ISSUED)
        assertThat(ex.getResponseCode())
            .isEqualTo(IssuedCouponErrorCode.ALREADY_ISSUED);

        // 중복이면 저장 로직 호출 x
        verify(issuedCouponRepository, never())
            .save(any(IssuedCoupon.class));

        // 발급 수량 감소 로직 호출 x
        verify(couponTemplateRepository, never())
            .decreaseIssueLimit(any());
    }

    // 발급 수량 소진
    /*
        사용자가 issueLimit가 0인 쿠폰을 발급받을려고하면
        ISSUE_LIMIT_EXHAUSTED 에러발생
     */
    @Test
    @DisplayName("쿠폰 발급 실패 - 발급 수량 소진")
    void issueCoupon_issueLimitExhausted_throwsException() {

        // given
        UUID userId = UUID.randomUUID();
        UUID templateId = UUID.randomUUID();

        // 발급 가능한 기간 + issueLimit 존재 -> 수량 감소 로직 실행
        CouponTemplate template = CouponTemplate.builder()
            .id(templateId)
            .issueLimit(1)
            .availableFrom(LocalDateTime.now().minusDays(1))
            .availableTo(LocalDateTime.now().plusDays(7))
            .build();

        given(couponTemplateRepository.findById(templateId))
            .willReturn(Optional.of(template));

        // 중복 발급 아님
        given(issuedCouponRepository.existsByUserIdAndCouponTemplateId(userId, templateId))
            .willReturn(false);

        // 수량 감소 실패(0이면 소진으로 판단)
        given(couponTemplateRepository.decreaseIssueLimit(templateId))
            .willReturn(0);

        // when
        BizException ex = assertThrows(
            BizException.class,
            () -> issuedCouponService.issueCoupon(userId, templateId)
        );

        // then
        assertThat(ex.getResponseCode())
            .isEqualTo(IssuedCouponErrorCode.ISSUE_LIMIT_EXHAUSTED);

        // 소진이면 저장 로직 호출 x
        verify(issuedCouponRepository, never())
            .save(any(IssuedCoupon.class));

        // 수량 감소를 시도했고, 결과가 0이면 소진으로 판단
        verify(couponTemplateRepository).
            decreaseIssueLimit(templateId);
    }

}
