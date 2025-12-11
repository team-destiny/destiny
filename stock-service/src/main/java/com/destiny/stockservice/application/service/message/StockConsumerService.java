package com.destiny.stockservice.application.service.message;

import com.destiny.stockservice.application.dto.StockCreateMessage;
import com.destiny.stockservice.application.dto.StockReduceCommand;
import com.destiny.stockservice.application.dto.StockReduceFailResult;
import com.destiny.stockservice.application.dto.StockReduceSuccessResult;
import com.destiny.stockservice.application.dto.StockRollbackCommand;
import com.destiny.stockservice.application.service.StockService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockConsumerService {

    private final StockProducerService stockProducerService;

    private final StockService stockService;

    private final ObjectMapper objectMapper;

    @SneakyThrows
    @RetryableTopic
    @KafkaListener(groupId = "orchestrator", topics = "stock-reduce-request")
    public void consumeStockMessage(String message) {

        StockReduceCommand command = objectMapper
            .readValue(message, StockReduceCommand.class);

        boolean success = stockService.validateAndDecrease(command.items());

        if (success) {
            stockProducerService.sendStockReduceSuccess(
                new StockReduceSuccessResult(command.orderId(), command.items())
            );
        } else {
            stockProducerService.sendStockReduceFail(
                new StockReduceFailResult(command.orderId())
            );
        }
    }

    @SneakyThrows
    @RetryableTopic
    @KafkaListener(groupId = "orchestrator", topics = "stock-reduce-rollback")
    public void consumeStockRollbackMessage(String stockRollbackCommand) {

        StockRollbackCommand command = objectMapper
            .readValue(stockRollbackCommand, StockRollbackCommand.class);

        stockService.rollbackQuantity(command.items());

        log.info("상품 재고 정보를 롤백했습니다. orderId={}", command.orderId());
    }

    @SneakyThrows
    @KafkaListener(groupId = "orchestrator", topics = "stock-create-message")
    public void consumeStockCreateMessage(String stockCreateMessage) {

        StockCreateMessage message = objectMapper
            .readValue(stockCreateMessage, StockCreateMessage.class);

        stockService.createStock(message);

        log.info("상품 재고 정보를 생성했습니다. productId={}", message.productId());
    }

    @DltHandler
    public void handleStockDlt(
        @Payload String payload,
        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
        @Header(KafkaHeaders.EXCEPTION_MESSAGE) String exceptionMessage
    ) {
        log.error("DLT Topic       : {}", topic);
        log.error("원본 메시지      : {}", payload);
        log.error("예외 메시지      : {}", exceptionMessage);

        if (topic.contains("stock-reduce-request")) {
            log.error("재고 차감 요청 처리 실패");
        } else if (topic.contains("stock-reduce-rollback")) {
            log.error("재고 롤백 처리 실패");
        } else if (topic.contains("stock-create-message")) {
            log.error("재고 생성 처리 실패");
        } else {
            log.error("알 수 없는 Stock 관련 DLT 메시지");
        }
    }

}
