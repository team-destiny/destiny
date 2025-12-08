package com.destiny.stockservice.application.service.message;

import com.destiny.stockservice.application.dto.StockReduceCommand;
import com.destiny.stockservice.application.dto.StockReduceFail;
import com.destiny.stockservice.application.dto.StockReduceSuccess;
import com.destiny.stockservice.application.dto.StockRollbackCommand;
import com.destiny.stockservice.application.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockConsumerService {

    private final StockProducerService stockProducerService;

    private final StockService stockService;

    @KafkaListener(groupId = "orchestrator", topics = "stock-reduce-request")
    public void consumeStockMessage(StockReduceCommand command) {

        boolean success = stockService.validateAndDecrease(command.orderedProducts());

        if (success) {
            stockProducerService.sendStockReduceSuccess(
                new StockReduceSuccess(
                    command.orderId(),
                    command.orderedProducts()
                )
            );
        } else {
            stockProducerService.sendStockReduceFail(
                new StockReduceFail(command.orderId())
            );
        }
    }

    @KafkaListener(groupId = "orchestrator", topics = "stock-reduce-rollback")
    public void consumeStockRollbackMessage(StockRollbackCommand command) {
       try {
           stockService.rollbackQuantity(command.orderedProducts());
           log.info("재고 롤백 완료: orderId={}", command.orderId());
       } catch (Exception e) {
            log.error("재고 롤백 실패: orderId={}, 수동 조치 필요", command.orderId(), e);
       }
    }
}
