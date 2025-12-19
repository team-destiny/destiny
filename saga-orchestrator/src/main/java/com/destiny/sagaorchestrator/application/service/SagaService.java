package com.destiny.sagaorchestrator.application.service;

import com.destiny.global.code.CommonErrorCode;
import com.destiny.global.exception.BizException;
import com.destiny.sagaorchestrator.domain.entity.SagaDlqMessage;
import com.destiny.sagaorchestrator.domain.entity.SagaState;
import com.destiny.sagaorchestrator.domain.repository.SagaDlqRepository;
import com.destiny.sagaorchestrator.domain.repository.SagaRepository;
import com.destiny.sagaorchestrator.infrastructure.auth.CustomUserDetails;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.command.NotificationDlqCommand;
import com.destiny.sagaorchestrator.infrastructure.messaging.producer.SagaProducer;
import com.destiny.sagaorchestrator.presentation.dto.response.SagaDlqResponse;
import com.destiny.sagaorchestrator.presentation.dto.response.SagaResponse;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SagaService {

    private final SagaRepository sagaRepository;
    private final SagaDlqRepository sagaDlqRepository;
    private final SagaProducer sagaProducer;

    public List<SagaResponse> sagaLogs(CustomUserDetails customUserDetails) {

        isAdmin(customUserDetails);

        List<SagaState> logs = sagaRepository.findAll();

        return logs.stream()
            .map(SagaResponse::from)
            .toList();
    }

    public List<SagaDlqResponse> dlqLogs(CustomUserDetails customUserDetails) {

        isAdmin(customUserDetails);

        List<SagaDlqMessage> logs = sagaDlqRepository.findAll();

        return logs.stream()
            .map(SagaDlqResponse::from)
            .toList();
    }

    public void onDlqMessage(ConsumerRecord<String, String> record, Map<String, Object> headers) {

        SagaDlqMessage message = SagaDlqMessage.fromKafka(record, headers);

        sagaProducer.publishDlqNotification(new NotificationDlqCommand(
            message.getOriginalTopic(),
            message.getDlqTopic(),
            message.getPartitionNumber(),
            message.getOffsetNumber(),
            message.getConsumerGroup(),
            message.getMessageKey(),
            message.getMessagePayload(),
            message.getExceptionType(),
            message.getStatus().toString(),
            message.getRetryCount(),
            message.getCreatedAt()
        ));

        sagaDlqRepository.save(message);
    }

    private void isAdmin(CustomUserDetails customUserDetails) {

        if (!customUserDetails.getUserRole().equalsIgnoreCase("MASTER")) {
            throw new BizException(CommonErrorCode.ACCESS_DENIED);
        }
    }
}
