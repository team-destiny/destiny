package com.destiny.sagaorchestrator.infrastructure.messaging.producer;

import com.destiny.sagaorchestrator.infrastructure.messaging.event.command.CouponValidateCommand;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.command.PaymentCreateCommand;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.command.ProductValidationCommand;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.command.StockReduceCommand;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.outcome.OrderCreateFailedEvent;
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
            log.info("send success -> product validate send success {}", message);

        } catch (JsonProcessingException e) {

            log.error("send failed -> product validate send failed {}" , e.getMessage());
        }
    }

    public void sendStockReduce(StockReduceCommand event) {

        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("stock-reduce-request", message);
            log.info("send success -> stock update send success {}", message);

        }catch (JsonProcessingException e){

            log.error("send failed -> stock update send failed {}" , e.getMessage());
        }
    }

    public void sendCouponValidate(CouponValidateCommand event) {

        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("coupon-use-request", message);
            log.info("send success ->  coupon success {}", message);

        } catch (JsonProcessingException e) {

            log.error("send failed -> coupon failed {}" , e.getMessage());
        }
    }

    public void sendPaymentRequest(PaymentCreateCommand event) {

        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("payment-confirm-request", message);
            log.info("send success -> payment confirm request {}", message);

        } catch (JsonProcessingException e) {

            log.error("send failed -> payment confirm request {}" , e.getMessage());
        }
    }

    /**
     *
     * @param event : 주문 생성 실패 EVENT DTO
     */
    public void sendOrderFailed(OrderCreateFailedEvent event) {

        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("order-create-failed", message);
            log.info("send success ->  order failed {}", message);

        } catch (JsonProcessingException e) {

            log.error("send failed -> order failed {}" , e.getMessage());
        }
    }

}
