package com.destiny.sagaorchestrator.infrastructure.messaging.consumer;

import com.destiny.sagaorchestrator.application.service.OrderCancelService;
import com.destiny.sagaorchestrator.application.service.OrderCreateService;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.request.OrderCancelRequestEvent;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.request.OrderCreateRequestEvent;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.request.PaymentFail;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.request.PaymentSuccess;
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
    private final OrderCreateService orderCreateService;
    private final OrderCancelService orderCancelService;

    @KafkaListener(topics = "order-create-request", groupId = "saga-orchestrator")
    public void onOrderCreate(String message) {

        try {
            log.info("[⭐️ JOIN SAGA SUCCESS] - ORDER CREATE : {}", message);

            OrderCreateRequestEvent event = objectMapper.readValue(
                message, OrderCreateRequestEvent.class);
            orderCreateService.createSaga(event);

        } catch (JsonProcessingException e) {

            log.info("[🔥️ JOIN SAGA FAIL JSON EXCEPTION] - ORDER CREATE : {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "product-validate-success", groupId = "saga-orchestrator")
    public void onProductValidateSuccess(String message) {

        try {
            log.info("[⭐️ JOIN SAGA SUCCESS] - PRODUCT VALIDATE SUCCESS : {}", message);

            ProductValidationSuccessResult event = objectMapper.readValue(
                message, ProductValidationSuccessResult.class);
            orderCreateService.productValidateSuccess(event);

        } catch (JsonProcessingException e) {

            log.info("[🔥️ JOIN SAGA FAIL JSON EXCEPTION] - PRODUCT VALIDATE SUCCESS : {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "product-validate-fail", groupId = "saga-orchestrator")
    public void onProductValidateFail(String message) {

        try {
            log.info("[❌ JOIN SAGA SUCCESS] - PRODUCT VALIDATE FAIL : {}", message);

            ProductValidateFailResult event = objectMapper.readValue(
                message, ProductValidateFailResult.class);
            orderCreateService.productValidateFailure(event);

        } catch (JsonProcessingException e) {

            log.info("[🔥️ JOIN SAGA FAIL JSON EXCEPTION] - PRODUCT VALIDATE FAIL : {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "stock-reduce-success", groupId = "saga-orchestrator")
    public void onStockReduceSuccess(String message) {

        try {
            log.info("[⭐️ JOIN SAGA SUCCESS] - STOCK REDUCE SUCCESS : {}", message);

            StockReduceSuccessResult event = objectMapper.readValue(
                message, StockReduceSuccessResult.class);
            orderCreateService.stockReduceSuccess(event);

        } catch (JsonProcessingException e) {

            log.info("[🔥️ JOIN SAGA FAIL JSON EXCEPTION] - STOCK REDUCE SUCCESS : {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "stock-reduce-fail", groupId = "saga-orchestrator")
    public void onStockReduceFail(String message) {

        try {
            log.info("[❌ JOIN SAGA SUCCESS] - STOCK REDUCE FAIL : {}", message);

            StockReduceFailResult event = objectMapper.readValue(
                message, StockReduceFailResult.class);
            orderCreateService.stockReduceFailure(event);

        } catch (JsonProcessingException e) {

            log.info("[🔥️ JOIN SAGA FAIL JSON EXCEPTION] - STOCK REDUCE FAIL : {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "coupon-use-success", groupId = "saga-orchestrator")
    public void onCouponUseSuccess(String message) {

        try {
            log.info("[⭐️ JOIN SAGA SUCCESS] - COUPON USE SUCCESS : {}", message);

            CouponUseSuccessResult event = objectMapper.readValue(
                message, CouponUseSuccessResult.class);
            orderCreateService.couponUseSuccess(event);

        } catch (JsonProcessingException e) {

            log.info("[🔥️ JOIN SAGA FAIL JSON EXCEPTION] - COUPON USE SUCCESS : {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "coupon-use-fail", groupId = "saga-orchestrator")
    public void onCouponUseFail(String message) {

        try {
            log.info("[❌ JOIN SAGA SUCCESS] - COUPON USE FAIL : {}", message);

            CouponUseFailResult event = objectMapper.readValue(
                message, CouponUseFailResult.class);
            orderCreateService.couponUseFailure(event);

        } catch (JsonProcessingException e) {

            log.info("[🔥️ JOIN SAGA FAIL JSON EXCEPTION] - COUPON USE FAIL : {}", e.getMessage());
         }
    }

    @KafkaListener(topics = "payment-create-success", groupId = "saga-orchestrator")
    public void onPaymentConfirmSuccess(String message) {

        try {
            log.info("[⭐️ JOIN SAGA SUCCESS] - PAYMENT CREATE SUCCESS : {}", message);

            PaymentConfirmSuccessResult event = objectMapper.readValue(
                message, PaymentConfirmSuccessResult.class);
            orderCreateService.paymentCreateSuccess(event);

        } catch (JsonProcessingException e) {

            log.info("[🔥️ JOIN SAGA FAIL JSON EXCEPTION] - PAYMENT CREATE SUCCESS : {}", e.getMessage());
        }

    }

    @KafkaListener(topics = "payment-create-fail", groupId = "saga-orchestrator")
    public void onPaymentConfirmFail(String message) {

        try {
            log.info("[❌ JOIN SAGA SUCCESS] - PAYMENT CREATE FAIL : {}", message);

            PaymentConfirmFailResult event = objectMapper.readValue(
                message, PaymentConfirmFailResult.class);
            orderCreateService.paymentCreateFailure(event);

        } catch (JsonProcessingException e){

            log.info("[🔥️ JOIN SAGA FAIL JSON EXCEPTION] - PAYMENT CREATE FAIL : {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "payment-success", groupId = "saga-orchestrator")
    public void onPaymentSuccess(String message) {

        try {
            log.info("[⭐️ JOIN SAGA SUCCESS] - PAYMENT SUCCESS : {}", message);

            PaymentSuccess event = objectMapper.readValue(
                message, PaymentSuccess.class);
            orderCreateService.paymentSuccess(event);

        } catch (JsonProcessingException e) {

            log.info("[🔥️ JOIN SAGA FAIL JSON EXCEPTION] - PAYMENT SUCCESS : {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "payment-fail", groupId = "saga-orchestrator")
    public void onPaymentFail(String message) {

        try {
            log.info("[❌ JOIN SAGA SUCCESS] - PAYMENT FAIL : {}", message);

            PaymentFail event = objectMapper.readValue(
                message, PaymentFail.class);
            orderCreateService.paymentFailed(event);

        } catch (JsonProcessingException e) {

            log.info("[🔥️ JOIN SAGA FAIL JSON EXCEPTION] - PAYMENT FAIL : {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "order-cancel-request", groupId = "saga-orchestrator")
    public void onOrderCancel(String message) {

        try {
            log.info("[⭐️ JOIN SAGA SUCCESS] - ORDER CANCEL : {}", message);

            OrderCancelRequestEvent event = objectMapper.readValue(
                message, OrderCancelRequestEvent.class);
            orderCancelService.cancelOrder(event);

        } catch (JsonProcessingException e) {

            log.info("[🔥 JOIN SAGA FAIL JSON EXCEPTION] - ORDER CANCEL : {}", e.getMessage());
        }
    }
}
