package com.destiny.sagaorchestrator.infrastructure.messaging.producer;

import com.destiny.sagaorchestrator.infrastructure.messaging.event.command.CartClearCommand;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.command.CouponUseRollbackCommand;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.command.CouponUseCommand;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.command.FailSendCommand;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.command.PaymentCancelCommand;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.command.PaymentCreateCommand;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.command.ProductValidationCommand;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.command.StockReduceCommand;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.command.StockRollbackCommand;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.command.SuccessSendCommand;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.outcome.OrderCreateFailedEvent;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.outcome.OrderCreateSuccessEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SagaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendProductValidate(ProductValidationCommand event) {

        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("product-validate-request", message);
            log.info("[🍏 SAGA-SERVICE -> PRODUCT-SERVICE SUCCESS] - PRODUCT VALIDATE : {}", message);

        } catch (JsonProcessingException e) {

            log.error("[❌ SAGA-SERVICE -> PRODUCT-SERVICE FAIL JSON EXCEPTION] - PRODUCT VALIDATE : {}", e.getMessage());
        }
    }

    public void sendStockReduce(StockReduceCommand event) {

        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("stock-reduce-request", message);
            log.info("[🍏 SAGA-SERVICE -> STOCK-SERVICE SUCCESS] - STOCK REDUCE : {}", message);

        } catch (JsonProcessingException e){

            log.error("[❌ SAGA-SERVICE -> STOCK-SERVICE FAIL JSON EXCEPTION] - STOCK REDUCE : {}", e.getMessage());
        }
    }

    public void sendCouponValidate(CouponUseCommand event) {

        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("coupon-use-request", message);
            log.info("[🍏 SAGA-SERVICE -> COUPON-SERVICE SUCCESS] - COUPON USE : {}", message);

        } catch (JsonProcessingException e) {

            log.error("[❌ SAGA-SERVICE -> STOCK-SERVICE FAIL JSON EXCEPTION] - COUPON USE : {}", e.getMessage());
        }
    }

    public void sendPaymentRequest(PaymentCreateCommand event) {

        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("payment-create-request", message);
            log.info("[🍏 SAGA-SERVICE -> PAYMENT-SERVICE SUCCESS] - PAYMENT CREATE : {}", message);

        } catch (JsonProcessingException e) {

            log.error("[❌ SAGA-SERVICE -> PAYMENT-SERVICE FAIL JSON EXCEPTION] - PAYMENT CREATE : {}", e.getMessage());
        }
    }

    public void sendCartClear(CartClearCommand event) {

        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("cart-clear-request", message);
            log.info("[🍏 SAGA-SERVICE -> CART-SERVICE SUCCESS] - CART CLEAR : {}", message);

        } catch (JsonProcessingException e) {

            log.error("[❌ SAGA-SERVICE -> CART-SERVICE FAIL JSON EXCEPTION] - CART CLEAR : {}", e.getMessage());
        }
    }

    public void sendOrderSuccess(OrderCreateSuccessEvent event) {

        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("order-create-success", message);
            log.info("[🍏 SAGA-SERVICE -> ORDER-SERVICE SUCCESS] - ORDER CREATE COMPLETED : {}", message);

        } catch (JsonProcessingException e) {

            log.error("[❌ SAGA-SERVICE -> ORDER-SERVICE FAIL JSON EXCEPTION] - ORDER CREATE COMPLETED : {}", e.getMessage());
        }
    }

    public void sendOrderFailed(OrderCreateFailedEvent event) {

        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("order-create-failed", message);
            log.info("[🍏 SAGA-SERVICE -> ORDER-SERVICE SUCCESS] - ORDER CREATE FAILED : {}", message);

        } catch (JsonProcessingException e) {

            log.error("[❌ SAGA-SERVICE -> ORDER-SERVICE FAIL JSON EXCEPTION] - ORDER CREATE FAILED: {}", e.getMessage());
        }
    }

    public void sendStockRollback(StockRollbackCommand event) {

        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("stock-reduce-rollback", message);
            log.info("[🍏 SAGA-SERVICE -> STOCK-SERVICE SUCCESS] - STOCK ROLLBACK : {}", message);

        } catch (JsonProcessingException e) {

            log.error("[❌ SAGA-SERVICE -> STOCK-SERVICE FAIL JSON EXCEPTION] - STOCK ROLLBACK: {}", e.getMessage());
        }
    }

    public void sendCouponRollback(CouponUseRollbackCommand event) {

        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("coupon-use-rollback", message);
            log.info("[🍏 SAGA-SERVICE -> COUPON-SERVICE SUCCESS] - COUPON ROLLBACK : {}", message);

        } catch (JsonProcessingException e) {

            log.error("[❌ SAGA-SERVICE -> COUPON-SERVICE FAIL JSON EXCEPTION] - COUPON ROLLBACK: {}", e.getMessage());
        }
    }

    public void sendSuccessMessage(SuccessSendCommand event) {

        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("success-send-message", message);
            log.info("[🍏 SAGA-SERVICE -> NOTIFICATION-SERVICE SUCCESS] - ORDER CREATE SUCCESS SEND : {}", message);

        } catch (JsonProcessingException e) {

            log.error("[❌ SAGA-SERVICE -> NOTIFICATION-SERVICE FAIL JSON EXCEPTION] - ORDER CREATE SUCCESS SEND: {}", e.getMessage());
        }
    }

    public void sendFailMessage(FailSendCommand event) {

        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("fail-send-message", message);
            log.info("[🍏 SAGA-SERVICE -> NOTIFICATION-SERVICE SUCCESS] - ORDER CREATE FAIL SEND : {}", message);

        } catch (JsonProcessingException e) {

            log.error("[❌ SAGA-SERVICE -> NOTIFICATION-SERVICE FAIL JSON EXCEPTION] - ORDER CREATE FAIL: {}", e.getMessage());
        }
    }

    public void cancelPayment(PaymentCancelCommand event) {

        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("payment-cancel-request", message);
            log.info("[🍎 SAGA-SERVICE -> PAYMENT-SERVICE SUCCESS] - PAYMENT CANCEL : {}", message);

        } catch (JsonProcessingException e) {

            log.info("[❌ SAGA-SERVICE -> PAYMENT-SERVICE FAIL JSON EXCEPTION] - PAYMENT CANCEL: {}", e.getMessage());
        }
    }
}
