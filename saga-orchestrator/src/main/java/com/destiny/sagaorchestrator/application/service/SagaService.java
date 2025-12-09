package com.destiny.sagaorchestrator.application.service;

import com.destiny.sagaorchestrator.domain.entity.SagaState;
import com.destiny.sagaorchestrator.domain.entity.SagaStatus;
import com.destiny.sagaorchestrator.domain.entity.SagaStep;
import com.destiny.sagaorchestrator.domain.repository.SagaRepository;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.command.CouponValidateCommand;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.command.ProductValidationCommand;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.command.StockReduceCommand;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.command.StockReduceItem;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.outcome.OrderCreateFailedEvent;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.request.OrderCreateRequestEvent;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.request.OrderCreateRequestEvent.OrderItemCreateRequestEvent;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.result.CouponUseFailResult;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.result.ProductValidateFailResult;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.result.ProductValidationMessageResult;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.result.ProductValidationResult;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.result.ProductValidationSuccessResult;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.result.StockReduceFailResult;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.result.StockReduceSuccessResult;
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

        SagaState saga = SagaState.of(
            event.cartId(),
            event.orderId(),
            event.userId(),
            event.couponId()
        );

        event.items().forEach(item -> {
            saga.getProductResults().put(
                item.productId(),
                new ProductValidationResult(
                    event.orderId(),
                    item.productId(),
                    item.stock(),
                    null,
                    null
                )
            );
        });

        sagaRepository.createSaga(saga);

        List<UUID> productIds = event.items()
            .stream()
            .map(OrderItemCreateRequestEvent::productId)
            .toList();

        sagaProducer.sendProductValidate(new ProductValidationCommand(saga.getOrderId(), productIds));
    }

    @Transactional
    public void productValidateSuccess(ProductValidationSuccessResult event) {
        SagaState saga = sagaRepository.findByOrderId(event.orderId());

        for (ProductValidationMessageResult msg : event.messages()) {
            ProductValidationResult old = saga.getProductResults().get(msg.productId());

            ProductValidationResult update = new ProductValidationResult(
                old.orderId(),
                old.productId(),
                old.stock(),
                msg.brandId(),
                msg.price()
            );

            saga.getProductResults().put(update.productId(), update);
        }

        Integer totalAmount = saga.getProductResults().values().stream()
                .mapToInt(item -> item.price() * item.stock()).sum();

        saga.updateOriginalAmount(totalAmount);
        saga.updateStep(SagaStep.PRODUCT_VALIDATED);
        saga.updateStatus(SagaStatus.PROGRESS);

        List<StockReduceItem> items = saga.getProductResults().values().stream()
                .map(result -> new StockReduceItem(
                    result.productId(),
                    result.stock()
                )).toList();

        sagaProducer.sendStockReduce(new StockReduceCommand(
            saga.getOrderId(),
            items
        ));
    }

    @Transactional
    public void productValidateFailure(ProductValidateFailResult event) {
        SagaState saga = sagaRepository.findByOrderId(event.orderId());
        saga.updateStep(SagaStep.PRODUCT_VALIDATION);
        saga.updateStatus(SagaStatus.FAILED);
        saga.updateFailureReason(event.message());
        saga.updateFailureStep("PRODUCT");

        // TODO : (주문 실패) 슬랙 서비스 쪽으로 메시지 발행 (주문 아이디, 주문자 아이디, 주문 실패한 이유등)

        sagaProducer.sendOrderFailed(new OrderCreateFailedEvent(
            saga.getSagaId(),
            saga.getOrderId(),
            "상품 데이터 가져오기 실패",
            "SAP-001",
            "PRODUCT-SERVICE"
        ));
    }

    @Transactional
    public void stockReduceSuccess(StockReduceSuccessResult event) {
        SagaState saga = sagaRepository.findByOrderId(event.orderId());
        saga.updateStep(SagaStep.STOCK_RESERVED);
        saga.updateStatus(SagaStatus.PROGRESS);

        sagaProducer.sendCouponValidate(new CouponValidateCommand(saga.getCouponId(), saga.getOriginalAmount()));
    }

    @Transactional
    public void stockReduceFailure(StockReduceFailResult event) {
        SagaState saga = sagaRepository.findByOrderId(event.orderId());
        saga.updateStep(SagaStep.STOCK_RESERVATION);
        saga.updateStatus(SagaStatus.FAILED);
        saga.updateFailureStep("STOCK");
        saga.updateFailureReason("재고 차감 실패(재고 부족)");
        sagaProducer.sendOrderFailed(new OrderCreateFailedEvent(
            saga.getSagaId(),
            saga.getOrderId(),
            "상품 재고 차감 실패",
            "SAS-001",
            "STOCK-SERVICE"
        ));
    }

    // TODO : 쿠폰 검증 및 쿠폰 할인율 가지고 오기
    @Transactional
    public void couponUseSuccess() {

    }

    @Transactional
    public void couponUseFailure(CouponUseFailResult event) {
        SagaState saga = sagaRepository.findByOrderId(event.orderId());
        saga.updateStep(SagaStep.COUPON_VALIDATION);
        saga.updateStatus(SagaStatus.FAILED);
        saga.updateFailureReason(event.message());
        saga.updateFailureStep("COUPON");

        sagaProducer.sendOrderFailed(new OrderCreateFailedEvent(
            saga.getSagaId(),
            event.orderId(),
            "쿠폰 적용하기 실패",
            "SAC-001",
            "COUPON-SERVICE"
        ));
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
