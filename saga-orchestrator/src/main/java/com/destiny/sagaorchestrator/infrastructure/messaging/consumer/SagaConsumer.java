package com.destiny.sagaorchestrator.infrastructure.messaging.consumer;

import com.destiny.sagaorchestrator.application.service.SagaService;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.request.OrderCreateRequestEvent;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.result.ProductValidateSuccessResult;
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

            ProductValidateSuccessResult event = objectMapper.readValue(
                message, ProductValidateSuccessResult.class);
            sagaService.productValidateSuccess(event);
        } catch (JsonProcessingException e) {

            log.error("Saga Service : product-validate-success json processing error", e);
        }
    }

    @KafkaListener(topics = "product-validate-fail", groupId = "saga-orchestrator")
    public void onProductValidateFail(String message) {

    }
}
