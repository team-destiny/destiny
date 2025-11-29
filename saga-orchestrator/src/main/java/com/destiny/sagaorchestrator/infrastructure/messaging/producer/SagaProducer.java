package com.destiny.sagaorchestrator.infrastructure.messaging.producer;

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
    private final ObjectMapper objectMapper = new ObjectMapper();

    // TODO : 상품 검증



    // TODO : 재고 차감



    // TODO : 결제 요청


    /**
     *
     * @param event : 주문 생성 실패 EVENT DTO
     */
    public void sendOrderFailed(OrderCreateFailedEvent event) {

        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("order.create.failed", message);
            log.info("send success ->  order failed {}", message);

        } catch (JsonProcessingException e) {

            log.error("send failed -> order failed {}" , e.getMessage());
        }
    }

}
