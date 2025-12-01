package com.destiny.sagaorchestrator.application.service;

import com.destiny.sagaorchestrator.domain.entity.SagaState;
import com.destiny.sagaorchestrator.domain.repository.SagaRepository;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.request.OrderCreateRequestEvent;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.result.ProductValidateResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SagaService {

    private final SagaRepository sagaRepository;

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
                // 아직 검증 전이므로 null값
                null
            );
        });

        sagaRepository.createSaga(saga);
        /*
        // 4) 상품 검증 이벤트 발행
        event.items().forEach(item -> {
            productProducer.send(
                item.productId(),
                item.itemPromotionId(),
                item.stock()
            );
        });
         */
    }

    // TODO : 상품 서비스 검증 및 상품 가격 가지고 오기
    @Transactional
    public void productValidate(ProductValidateResult result) {


    }

    // TODO : 쿠폰 검증 및 쿠폰 할인율 가지고 오기
    @Transactional
    public void couponValidate() {

    }

    // TODO : 재고 차감
    @Transactional
    public void stockUpdate() {

    }

    // TODO : 결제
    @Transactional
    public void paymentOrder() {

    }
}
