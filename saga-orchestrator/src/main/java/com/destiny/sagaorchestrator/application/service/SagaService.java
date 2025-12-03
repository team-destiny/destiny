package com.destiny.sagaorchestrator.application.service;

import com.destiny.sagaorchestrator.domain.entity.SagaState;
import com.destiny.sagaorchestrator.domain.repository.SagaRepository;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.command.CouponValidateCommand;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.command.ProductValidateCommand;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.request.OrderCreateRequestEvent;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.request.OrderCreateRequestEvent.OrderItemCreateRequestEvent;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.result.ProductValidateResult;
import com.destiny.sagaorchestrator.infrastructure.messaging.producer.SagaProducer;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SagaService {

    private final SagaRepository sagaRepository;
    private final SagaProducer sagaProducer;

    @Transactional
    public void createSaga(OrderCreateRequestEvent event) {

        // 1) 사가 초기 생성
        SagaState saga = SagaState.of(
            event.orderId(),
            event.userId(),
            event.couponId()
        );

        // 2) productId 기반으로 빈 ProductValidateResult 등록
        event.items().forEach(item -> {
            saga.getProductResults().put(
                item.productId(),
                // 아직 검증 전이므로 하드 코딩 직접 값 넣어주었음.
                new  ProductValidateResult(
                    item.productId(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
                )
            );
        });

        sagaRepository.createSaga(saga);

        // 3-1) 주문 아이템에서 productId 추출
        List<UUID> productId = event.items()
            .stream()
            .map(OrderItemCreateRequestEvent::productId)
            .toList();

        // 3-2) 상품 검증 토픽 발행
        sagaProducer.sendProductValidate(new ProductValidateCommand(productId));

        // etc : 쿠폰 검증 이벤트부터 먼저 테스트.
        // 이후 아래 메서드는 상품 검증 이후 실행해야 하는 메서드로 productValidate로 이동 예정
        sagaProducer.sendCouponValidate(new CouponValidateCommand(event.couponId(), null));
    }

    // TODO : 상품 서비스 검증 및 상품 가격 가지고 오기
    @Transactional
    public void productValidate(ProductValidateResult event) {


    }


    // TODO : 재고 차감
    @Transactional
    public void stockUpdate() {

    }

    // TODO : 쿠폰 검증 및 쿠폰 할인율 가지고 오기
    @Transactional
    public void couponValidate() {

    }


    // TODO : 결제
    @Transactional
    public void paymentOrder() {

    }
}
