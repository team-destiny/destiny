package com.destiny.sagaorchestrator.infrastructure.messaging.consumer;

import com.destiny.sagaorchestrator.domain.entity.SagaDlqMessage;
import com.destiny.sagaorchestrator.domain.repository.SagaDlqRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SagaDlqConsumer {

    private final SagaDlqRepository sagaDlqRepository;

    @KafkaListener(topicPattern = ".*\\.DLQ", groupId = "saga-orchestrator-dlq")
    public void consumeDlq(
        ConsumerRecord<String, String> record,
        @Headers Map<String, Object> headers
    ) {
        SagaDlqMessage message = SagaDlqMessage.fromKafka(record, headers);

        sagaDlqRepository.save(message);
    }

}
