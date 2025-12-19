package com.destiny.couponservice;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import com.destiny.couponservice.application.service.impl.CouponTemplateServiceImpl;
import com.destiny.couponservice.domain.entity.CouponTemplate;
import com.destiny.couponservice.domain.enums.DiscountType;
import com.destiny.couponservice.domain.repository.CouponTemplateRepository;
import com.destiny.couponservice.presentation.dto.request.CouponTemplateCreateRequest;
import com.destiny.couponservice.presentation.dto.response.CouponTemplateCreateResponse;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CouponTemplateTest {

    @InjectMocks
    private CouponTemplateServiceImpl couponTemplateServiceImpl;

    @Mock
    private CouponTemplateRepository couponTemplateRepository;

    @Test
    @DisplayName("쿠폰 템플릿 생성 성공")
    void create_success() {

        // given (테스트 준비 단계)
        LocalDateTime from = LocalDateTime.now().minusDays(1);
        LocalDateTime to = LocalDateTime.now().plusDays(7);

        CouponTemplateCreateRequest req = CouponTemplateCreateRequest.builder()
            .code("BLACK FRIDAY2025")
            .name("블랙 프라이데이 3000원 할인 쿠폰")
            .discountType(DiscountType.FIXED)
            .discountValue(3000)
            .minOrderAmount(10000)
            .maxDiscountAmount(null) // FIXED 할인은 maxDiscountAmount null 가능
            .issueLimit(100)
            .availableFrom(from)
            .availableTo(to)
            .build();

        // 이 코드의 쿠폰은 "중복 아님" 이라고 가정
        given(couponTemplateRepository.existsByCode("BLACK FRIDAY2025")).willReturn(false);

        CouponTemplate saved = CouponTemplate.builder()
            .id(UUID.randomUUID())
            .code(req.getCode())
            .name(req.getName())
            .discountType(req.getDiscountType())
            .discountValue(req.getDiscountValue())
            .minOrderAmount(req.getMinOrderAmount())
            .maxDiscountAmount(req.getMaxDiscountAmount())
            .issueLimit(req.getIssueLimit())
            .availableFrom(req.getAvailableFrom())
            .availableTo(req.getAvailableTo())
            .build();


        // Service가 create()를 호출하면 saved 객체를 돌려주게 설정
        given(couponTemplateRepository.create(any(CouponTemplate.class))).willReturn(saved);

        // when (테스트 실행)
        CouponTemplateCreateResponse response = couponTemplateServiceImpl.create(req);

        // then (결과 검증)
        assertThat(response.getId()).isEqualTo(saved.getId());
        assertThat(response.getCode()).isEqualTo("BLACK FRIDAY2025");
        assertThat(response.getDiscountType()).isEqualTo(DiscountType.FIXED);

        verify(couponTemplateRepository).existsByCode("BLACK FRIDAY2025");

        // ArgumentCaptor : 전달된 인자 값 검증
        ArgumentCaptor<CouponTemplate> captor = ArgumentCaptor.forClass(CouponTemplate.class);
        verify(couponTemplateRepository).create(captor.capture());

        CouponTemplate passed = captor.getValue();
        assertThat(passed.getCode()).isEqualTo("BLACK FRIDAY2025");
        assertThat(passed.getDiscountValue()).isEqualTo(3000);
        assertThat(passed.getMinOrderAmount()).isEqualTo(10000);
    }
}
