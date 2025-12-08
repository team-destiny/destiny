package com.destiny.stockservice.application.service.message;

import com.destiny.stockservice.application.dto.StockReduceCommand;
import com.destiny.stockservice.application.dto.StockReduceFail;
import com.destiny.stockservice.application.dto.StockReduceSuccess;
import com.destiny.stockservice.application.dto.StockRollbackCommand;
import com.destiny.stockservice.application.service.StockService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockConsumerService {

    private final StockProducerService stockProducerService;

    private final StockService stockService;

    private final ObjectMapper objectMapper;

    @SneakyThrows
    @KafkaListener(groupId = "orchestrator", topics = "stock-reduce-request")
    public void consumeStockMessage(String message) {

        StockReduceCommand command = objectMapper
            .readValue(message, StockReduceCommand.class);

        boolean success = stockService.validateAndDecrease(command.items());

        if (success) {
            stockProducerService.sendStockReduceSuccess(
                new StockReduceSuccess(command.orderId(), command.items())
            );
        } else {
            stockProducerService.sendStockReduceFail(
                new StockReduceFail(command.orderId())
            );
        }
    }

    @SneakyThrows
    @KafkaListener(groupId = "orchestrator", topics = "stock-reduce-rollback")
    public void consumeStockRollbackMessage(String stockRollbackCommand) {

        StockRollbackCommand command = objectMapper
            .readValue(stockRollbackCommand, StockRollbackCommand.class);

        try {
           stockService.rollbackQuantity(command.items());
           log.info("재고 롤백 완료: orderId={}", command.orderId());
       } catch (Exception e) {
            log.error("재고 롤백 실패: orderId={}, 수동 조치 필요", command.orderId(), e);
       }
    }
}
