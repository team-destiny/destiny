package com.destiny.sagaorchestrator.application.service;

import com.destiny.sagaorchestrator.domain.entity.SagaState;
import com.destiny.sagaorchestrator.domain.entity.SagaStatus;
import com.destiny.sagaorchestrator.domain.entity.SagaStep;
import com.destiny.sagaorchestrator.domain.repository.SagaRepository;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.command.CouponCancelCommand;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.command.PaymentCancelCommand;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.request.OrderCancelRequestEvent;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.result.PaymentCancelFailResult;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.result.PaymentCancelSuccessResult;
import com.destiny.sagaorchestrator.infrastructure.messaging.producer.SagaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCancelService {

    private final SagaRepository sagaRepository;
    private final SagaProducer sagaProducer;

    @Transactional
    public void cancelOrder(OrderCancelRequestEvent event) {
        SagaState saga = sagaRepository.findByOrderId(event.orderId());
        saga.updateStep(SagaStep.ORDER_CANCEL_REQUEST);
        saga.updateStatus(SagaStatus.CANCEL_REQUEST);

        sagaProducer.cancelPayment(new PaymentCancelCommand(
            saga.getSagaId(),
            saga.getOrderId(),
            saga.getUserId(),
            saga.getFinalAmount()
        ));
    }

    @Transactional
    public void cancelPaymentSuccess(PaymentCancelSuccessResult event) {
        SagaState saga = sagaRepository.findById(event.sagaId());
        saga.updateStep(SagaStep.PAYMENT_CANCEL_SUCCESS);
        saga.updateStatus(SagaStatus.CANCEL_PROGRESS);

        sagaProducer.cancelCoupon(new CouponCancelCommand(
            saga.getSagaId(),
            saga.getOrderId(),
            saga.getUserId(),
            saga.getCouponId()
        ));
    }

    @Transactional
    public void cancelPaymentFail(PaymentCancelFailResult event) {
        SagaState saga = sagaRepository.findById(event.sagaId());
        saga.updateStep(SagaStep.PAYMENT_CANCEL_FAIL);
        saga.updateStatus(SagaStatus.CANCEL_FAILED);
    }

    @Transactional
    public void cancelCouponSuccess() {

    }

    @Transactional
    public void cancelCouponFail() {

    }
}
