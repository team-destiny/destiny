package com.destiny.sagaorchestrator.infrastructure.messaging.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaErrorHandlerConfig {

    /**
     * CommonErrorHandler
     *
     * 메시지 처리 중 예외가 발생했을 때
     * Kafka가 어떻게 행동할지를 정의.
     *
     */
    @Bean
    public CommonErrorHandler commonErrorHandler(
        DeadLetterPublishingRecoverer deadLetterPublishingRecoverer
    ) {

        /**
         *  FixedBackOff
         *
         *  - interval : 재시도 간격 (ms)
         *  - maxAttempts : 재시도 횟수
         *
         *  이커머스 기준 :
         *  - 너무 길면 처리 지연 될 거 같음.
         *  - 너무 짧으면 외부 시스템 회복 전 재시도
         *
         *  그래서 500 ms * 3회가 현실적인 타협이라 생각했음.
         */
        FixedBackOff backOff = new FixedBackOff(500L, 3L);

        return new DefaultErrorHandler(deadLetterPublishingRecoverer, backOff);
    }

}
