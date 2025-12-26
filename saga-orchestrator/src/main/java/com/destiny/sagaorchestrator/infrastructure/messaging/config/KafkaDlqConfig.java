package com.destiny.sagaorchestrator.infrastructure.messaging.config;

import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;

@Configuration
public class KafkaDlqConfig {

    /**
     * DeadLetterPublishingRecoverer
     *
     * Retry를 모두 소진한 메시지를
     * 어떤 DLQ 토픽으로 보낼지 정의하는 컴포넌트이다.
     *
     * 이 Bean이 없으면 DLQ는 절대 동작하지 않는다.
     */
    @Bean
    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(
        KafkaTemplate<String, String> kafkaTemplate
    ) {
        return new DeadLetterPublishingRecoverer(kafkaTemplate, (record, exception) -> {

            /**
             * DLQ 토픽 네이밍 규칙
             *
             * 원본 토픽 : order-create-request
             * DLQ 토픽 : order-create-request.dlq
             */
            return new TopicPartition(
                record.topic() + ".DLQ",
                record.partition()
            );
        });
    }
}
