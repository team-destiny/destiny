package com.destiny.sagaorchestrator.application.service;

import com.destiny.sagaorchestrator.domain.entity.SagaState;
import com.destiny.sagaorchestrator.domain.repository.SagaRepository;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.command.CouponValidateCommand;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.command.ProductValidationCommand;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.request.OrderCreateRequestEvent;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.request.OrderCreateRequestEvent.OrderItemCreateRequestEvent;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.result.ProductValidateFailResult;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.result.ProductValidateSuccessResult;
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
                new ProductValidateSuccessResult(
                    event.orderId(),
                    item.productId(),
                    null,
                    null
                )
            );
        });

        sagaRepository.createSaga(saga);

        // 3-1) 주문 아이템에서 productId 추출
        List<UUID> productIds = event.items()
            .stream()
            .map(OrderItemCreateRequestEvent::productId)
            .toList();

        // 3-2) 상품 검증 토픽 발행
        sagaProducer.sendProductValidate(new ProductValidationCommand(saga.getOrderId(), productIds));

    }

    // TODO : 상품 서비스 검증 및 상품 가격 가지고 오기
    @Transactional
    public void productValidateSuccess(ProductValidateSuccessResult event) {

        SagaState saga = sagaRepository.findByOrderId(event.orderId());

        saga.getProductResults().put(
            event.productId(),
            new ProductValidateSuccessResult(
                event.orderId(),
                event.productId(),
                event.brandId(),
                event.price()
            )
        );
    }

    @Transactional
    public void productValidateFailure(ProductValidateFailResult event) {

    }


    // TODO : 재고 차감
    @Transactional
    public void stockUpdateSuccess() {


        sagaProducer.sendCouponValidate(new CouponValidateCommand(null, null));
    }

    @Transactional
    public void stockUpdateFailure() {

    }

    // TODO : 쿠폰 검증 및 쿠폰 할인율 가지고 오기
    @Transactional
    public void couponUseSuccess() {

    }

    @Transactional
    public void couponUseFailure() {

    }

    // TODO : 결제
    @Transactional
    public void paymentSuccess() {

        // TODO: 결제 생성 성공 시 장바구니 비우는 토픽 발행

        // TODO: 결제 생성 성공 시 주문 서비스로 주문 프로세스 완료 토픽 발행
    }

    @Transactional
    public void paymentFailure() {

    }
}
