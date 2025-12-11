package com.destiny.sagaorchestrator.infrastructure.messaging.consumer;

import com.destiny.sagaorchestrator.application.service.SagaService;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.request.OrderCreateRequestEvent;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.result.CouponUseFailResult;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.result.CouponUseSuccessResult;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.result.PaymentConfirmFailResult;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.result.PaymentConfirmSuccessResult;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.result.ProductValidateFailResult;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.result.ProductValidationSuccessResult;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.result.StockReduceFailResult;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.result.StockReduceSuccessResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SagaConsumer {

    private final ObjectMapper objectMapper;
    private final SagaService sagaService;

    @KafkaListener(topics = "order-create-request", groupId = "saga-orchestrator")
    public void onOrderCreate(String message) {

        try {
            log.info("Join Saga Service : order-create-request");
            OrderCreateRequestEvent event = objectMapper.readValue(
                message, OrderCreateRequestEvent.class);

            sagaService.createSaga(event);
            log.info("Saga Service : order-create-request has been created");
        } catch (JsonProcessingException e) {

            log.error("Saga Service : order-create-request json processing error", e);
        }
    }

    @KafkaListener(topics = "product-validate-success", groupId = "saga-orchestrator")
    public void onProductValidateSuccess(String message) {

        try {
            log.info("Join Saga Service : product-validate-success");

            ProductValidationSuccessResult event = objectMapper.readValue(
                message, ProductValidationSuccessResult.class);
            sagaService.productValidateSuccess(event);
        } catch (JsonProcessingException e) {

            log.error("Saga Service : product-validate-success json processing error", e);
        }
    }

    @KafkaListener(topics = "product-validate-fail", groupId = "saga-orchestrator")
    public void onProductValidateFail(String message) {

        try {
            log.info("Join Saga Service : product-validate-fail");

            ProductValidateFailResult event = objectMapper.readValue(
                message, ProductValidateFailResult.class);
            sagaService.productValidateFailure(event);
        } catch (JsonProcessingException e) {

            log.error("Saga Service : product-validate-fail json processing error", e);
        }
    }

    @KafkaListener(topics = "stock-reduce-success", groupId = "saga-orchestrator")
    public void onStockReduceSuccess(String message) {

        try {
            log.info("Join Saga Service : stock-reduce-success");

            StockReduceSuccessResult event = objectMapper.readValue(
                message, StockReduceSuccessResult.class);
            sagaService.stockReduceSuccess(event);
        } catch (JsonProcessingException e) {

            log.error("Saga Service : stock-reduce-success json processing error", e);
        }
    }

    @KafkaListener(topics = "stock-reduce-fail", groupId = "saga-orchestrator")
    public void onStockReduceFail(String message) {

        try {
            log.info("Join Saga Service : stock-reduce-fail");
            StockReduceFailResult event = objectMapper.readValue(
                message, StockReduceFailResult.class);
        } catch (JsonProcessingException e) {

            log.error("Saga Service : stock-reduce-fail json processing error", e);
        }

    }

    @KafkaListener(topics = "coupon-use-success", groupId = "saga-orchestrator")
    public void onCouponUseSuccess(String message) {

        try {
            log.info("Join Saga Service : coupon-use-success");
            CouponUseSuccessResult event = objectMapper.readValue(
                message, CouponUseSuccessResult.class);

            sagaService.couponUseSuccess(event);
        } catch (JsonProcessingException e) {

            log.error("Saga Service : coupon-use-success json processing error", e);
        }

    }

    @KafkaListener(topics = "coupon-use-fail", groupId = "saga-orchestrator")
    public void onCouponUseFail(String message) {

        try {
            log.info("Join Saga Service : coupon-use-fail");
            CouponUseFailResult event = objectMapper.readValue(
                message, CouponUseFailResult.class);
        } catch (JsonProcessingException e) {

            log.error("Saga Service : coupon-use-fail json processing error", e);
         }

    }

    @KafkaListener(topics = "payment-confirm-success", groupId = "saga-orchestrator")
    public void onPaymentConfirmSuccess(String message) {

        try {
            log.info("Join Saga Service : payment-confirm-success");
            PaymentConfirmSuccessResult event = objectMapper.readValue(
                message, PaymentConfirmSuccessResult.class);

        } catch (JsonProcessingException e) {
            log.error("Saga Service : payment-confirm-success json processing error", e);
        }

    }

    @KafkaListener(topics = "payment-confirm-fail", groupId = "saga-orchestrator")
    public void onPaymentConfirmFail(String message) {

        try {
            log.info("Join Saga Service : payment-confirm-fail");
            PaymentConfirmFailResult event = objectMapper.readValue(
                message, PaymentConfirmFailResult.class);

        } catch (JsonProcessingException e){
            log.error("Saga Service : payment-confirm-fail json processing error", e);
        }
    }

}
