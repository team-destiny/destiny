package com.destiny.sagaorchestrator.infrastructure.messaging.consumer;

import com.destiny.sagaorchestrator.application.service.SagaService;
import com.destiny.sagaorchestrator.domain.entity.SagaDlqMessage;
import com.destiny.sagaorchestrator.domain.repository.SagaDlqRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SagaDlqConsumer {

    private final SagaDlqRepository sagaDlqRepository;
    private final SagaService sagaService;

    @KafkaListener(topicPattern = ".*\\.DLQ", groupId = "saga-orchestrator-dlq")
    public void consumeDlq(
        ConsumerRecord<String, String> record,
        @Headers Map<String, Object> headers
    ) {
        sagaService.onDlqMessage(record, headers);
        log.info("[📌 JOIN SAGA DLQ] TOPIC : {}", record.topic());
    }

}
