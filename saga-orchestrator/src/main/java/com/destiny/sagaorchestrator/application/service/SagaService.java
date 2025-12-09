package com.destiny.sagaorchestrator.application.service;

import com.destiny.sagaorchestrator.domain.entity.SagaState;
import com.destiny.sagaorchestrator.domain.entity.SagaStatus;
import com.destiny.sagaorchestrator.domain.entity.SagaStep;
import com.destiny.sagaorchestrator.domain.repository.SagaRepository;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.command.CartClearCommand;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.command.CouponValidateCommand;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.command.PaymentCreateCommand;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.command.ProductValidationCommand;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.command.StockReduceCommand;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.command.StockReduceItem;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.outcome.OrderCreateFailedEvent;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.outcome.OrderCreateSuccessEvent;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.outcome.OrderCreateSuccessEvent.OrderItem;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.request.OrderCreateRequestEvent;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.request.OrderCreateRequestEvent.OrderItemCreateRequestEvent;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.result.CouponUseFailResult;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.result.CouponUseSuccessResult;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.result.PaymentConfirmFailResult;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.result.PaymentConfirmSuccessResult;
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
import org.springframework.web.servlet.View;

@Service
@Slf4j
@RequiredArgsConstructor
public class SagaService {

    private final SagaRepository sagaRepository;
    private final SagaProducer sagaProducer;
    private final View error;

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

        sagaProducer.sendProductValidate(
            new ProductValidationCommand(saga.getOrderId(), productIds));
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
        saga.updateFinalAmount(saga.getOriginalAmount());

        if (saga.getCouponId() != null) {
            sagaProducer.sendCouponValidate(
                new CouponValidateCommand(saga.getOrderId(), saga.getCouponId(),
                    saga.getOriginalAmount()));
        }

        sagaProducer.sendPaymentRequest(
            new PaymentCreateCommand(saga.getOrderId(), saga.getUserId(), saga.getFinalAmount()));

    }

    @Transactional
    public void stockReduceFailure(StockReduceFailResult event) {
        SagaState saga = sagaRepository.findByOrderId(event.orderId());
        saga.updateStep(SagaStep.STOCK_RESERVATION);
        saga.updateStatus(SagaStatus.FAILED);
        saga.updateFailureStep("STOCK");
        saga.updateFailureReason("재고 차감 실패(재고 부족)");

        sendOrderCreateFailMessage(
            event,
            saga,
            "해당 상품 재고가 부족합니다.",
            "SAS-001",
            "STOCK-SERVICE"
        );

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
    public void couponUseSuccess(CouponUseSuccessResult event) {

        SagaState saga = sagaRepository.findByOrderId(event.orderId());
        saga.updateStep(SagaStep.COUPON_VALIDATED);
        saga.updateStatus(SagaStatus.PROGRESS);
        saga.updateFinalAmount(event.finalAmount());
        saga.updateDiscountAmount(saga.getOriginalAmount() - event.finalAmount());

        // TODO : 결제 생성 요청 이벤트 발행
        sagaProducer.sendPaymentRequest(
            new PaymentCreateCommand(saga.getOrderId(), saga.getUserId(), saga.getFinalAmount()));
    }

    @Transactional
    public void couponUseFailure(CouponUseFailResult event) {
        SagaState saga = sagaRepository.findByOrderId(event.orderId());
        saga.updateStep(SagaStep.COUPON_VALIDATION);
        saga.updateStatus(SagaStatus.FAILED);
        saga.updateFailureReason(event.errorMessage());
        saga.updateFailureStep("COUPON");

        sendOrderCreateFailMessage(
            event,
            saga,
            "유효한 쿠폰이 아닙니다.",
            "SAC-001",
            "COUPON-SERVICE");
    }

    // TODO : 결제

    @Transactional
    public void paymentSuccess(PaymentConfirmSuccessResult event) {

        SagaState saga = sagaRepository.findByOrderId(event.orderId());

        log.info(
            "ㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁ결제 서비스 -> 사가 서비스 결제 생성 성공 요청 로직 실행ㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁ");

        if (saga.getCartId() != null) {
            sagaProducer.sendCartClear(new CartClearCommand(saga.getCartId()));
        }

        List<OrderItem> items = saga.getProductResults().values().stream()
            .map(r -> new OrderItem(
                r.productId(),
                r.brandId(),
                r.price(),
                r.stock()
            )).toList();

        sagaProducer.sendOrderSuccess(new OrderCreateSuccessEvent(
            saga.getOrderId(),
            saga.getUserId(),
            saga.getCouponId(),
            saga.getOriginalAmount(),
            saga.getDiscountAmount(),
            saga.getFinalAmount(),
            items
        ));

    }

    @Transactional
    public void paymentFailure(PaymentConfirmFailResult event) {

    }

    private void sendOrderCreateFailMessage(
        Object event,
        SagaState saga,
        String failReason,
        String errorCode,
        String failedService
    ) {
        sagaProducer.sendOrderFailed(new OrderCreateFailedEvent(
            saga.getSagaId(),
            saga.getOrderId(),
            failReason,
            errorCode,
            failedService
        ));
    }
}
