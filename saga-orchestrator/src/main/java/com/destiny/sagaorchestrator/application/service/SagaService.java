package com.destiny.sagaorchestrator.application.service;

import com.destiny.sagaorchestrator.domain.entity.SagaState;
import com.destiny.sagaorchestrator.domain.entity.SagaStatus;
import com.destiny.sagaorchestrator.domain.entity.SagaStep;
import com.destiny.sagaorchestrator.domain.repository.SagaRepository;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.command.CartClearCommand;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.command.CouponValidateCommand;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.command.FailSendCommand;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.command.PaymentCreateCommand;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.command.ProductValidationCommand;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.command.StockReduceCommand;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.command.StockReduceItem;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.command.StockRollbackCommand;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.command.SuccessSendCommand;
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

        sagaProducer.sendProductValidate(
            new ProductValidationCommand(saga.getOrderId(), productIds));
    }

    @Transactional
    public void productValidateSuccess(ProductValidationSuccessResult event) {
        SagaState saga = sagaRepository.findByOrderId(event.orderId());
        saga.updateStep(SagaStep.PRODUCT_VALIDATION_SUCCESS);
        saga.updateStatus(SagaStatus.PROGRESS);

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
        saga.updateStep(SagaStep.PRODUCT_VALIDATION_FAIL);
        saga.updateStatus(SagaStatus.FAILED);
        saga.updateFailureReason(event.message());

        // TODO : (주문 실패) 슬랙 서비스 쪽으로 메시지 발행 (주문 아이디, 주문자 아이디, 주문 실패한 이유등)
        sendOrderCreateFailMessage(
            event,
            saga,
            "유효하지 않은 상품입니다.",
            "SAP-001",
            "PRODUCT-SERVICE");

        sendSlackFailMessage(
            saga,
            "SAP-001",
            "유효하지 않은 상품입니다.",
            "PRODUCT-SERVICE"
        );

    }

    @Transactional
    public void stockReduceSuccess(StockReduceSuccessResult event) {
        SagaState saga = sagaRepository.findByOrderId(event.orderId());
        saga.updateStep(SagaStep.STOCK_REDUCE_SUCCESS);
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
        saga.updateStep(SagaStep.STOCK_REDUCE_FAIL);
        saga.updateStatus(SagaStatus.FAILED);
        saga.updateFailureReason("재고 차감 실패(재고 부족)");

        sendOrderCreateFailMessage(
            event,
            saga,
            "해당 상품 재고가 부족합니다.",
            "SAS-001",
            "STOCK-SERVICE"
        );

        sendSlackFailMessage(
            saga,
            "SAS-001",
            "상품 재고 부족",
            "STOCK-SERVICE"
        );
    }

    @Transactional
    public void couponUseSuccess(CouponUseSuccessResult event) {

        SagaState saga = sagaRepository.findByOrderId(event.orderId());
        saga.updateStep(SagaStep.COUPON_VALIDATION_SUCCESS);
        saga.updateStatus(SagaStatus.PROGRESS);
        saga.updateFinalAmount(event.finalAmount());
        saga.updateDiscountAmount(saga.getOriginalAmount() - event.finalAmount());

        sagaProducer.sendPaymentRequest(
            new PaymentCreateCommand(saga.getOrderId(), saga.getUserId(), saga.getFinalAmount()));
    }

    @Transactional
    public void couponUseFailure(CouponUseFailResult event) {
        SagaState saga = sagaRepository.findByOrderId(event.orderId());
        saga.updateStep(SagaStep.COUPON_VALIDATION_FAIL);
        saga.updateStatus(SagaStatus.FAILED);
        saga.updateFailureReason(event.errorMessage());

        sendStockRollback(saga);

        sendOrderCreateFailMessage(
            event,
            saga,
            "유효한 쿠폰이 아닙니다.",
            "SAC-001",
            "COUPON-SERVICE");

        sendSlackFailMessage(
            saga,
            "SAC-001",
            "쿠폰 사용에 실패하였습니다.",
            "COUPON-SERVICE");
    }

    @Transactional
    public void paymentCreateSuccess(PaymentConfirmSuccessResult event) {
        SagaState saga = sagaRepository.findByOrderId(event.orderId());
        saga.updateStep(SagaStep.PAYMENT_SUCCESS);
        saga.updateStatus(SagaStatus.COMPLETED);

        if (saga.getCartId() != null) {
            sagaProducer.sendCartClear(new CartClearCommand(saga.getCartId()));
        }

        List<SuccessSendCommand.OrderItem> slackItems =
            saga.getProductResults().values().stream()
                    .map(r -> new SuccessSendCommand.OrderItem(
                        r.productId(),
                        r.brandId(),
                        r.stock(),
                        r.price()
                    )).toList();

        sagaProducer.sendSuccessMessage(new SuccessSendCommand(
            saga.getOrderId(),
            saga.getUserId(),
            slackItems
        ));

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
    public void paymentCreateFailure(PaymentConfirmFailResult event) {
        SagaState saga = sagaRepository.findByOrderId(event.orderId());
        saga.updateStep(SagaStep.PAYMENT_FAIL);
        saga.updateStatus(SagaStatus.FAILED);
        saga.updateFailureReason(event.errorMessage());

        sendStockRollback(saga);

        sendOrderCreateFailMessage(
            event,
            saga,
            "결제 생성 요청 실패하였습니다.",
            "SAM-001",
            "PAYMENT-SERVICE"
        );

        sendSlackFailMessage(
            saga,
            "SAM-001",
            "결제 요청이 실패하였습니다.",
            "PAYMENT-SERVICE");
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

    private void sendStockRollback(SagaState saga) {
        List<StockReduceItem> items = saga.getProductResults().values().stream()
            .map(r -> new StockReduceItem(
                r.productId(),
                r.stock()
            )).toList();

        sagaProducer.sendStockRollback(new StockRollbackCommand(
            saga.getOrderId(),
            items
        ));
    }

    private void sendSlackFailMessage(
        SagaState saga,
        String errorCode,
        String detailMessage,
        String failService
    ) {
        sagaProducer.sendFailMessage(new FailSendCommand(
            saga.getOrderId(),
            saga.getStep().toString(),
            errorCode,
            saga.getFailureReason(),
            detailMessage,
            failService
        ));
    }
}
