package com.destiny.stockservice.application.service.message;

import com.destiny.stockservice.application.dto.StockDecreaseCommand;
import com.destiny.stockservice.application.dto.StockDecreaseFail;
import com.destiny.stockservice.application.dto.StockDecreaseSuccess;
import com.destiny.stockservice.application.dto.StockRollbackCommand;
import com.destiny.stockservice.application.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockConsumerService {

    private final StockProducerService stockProducerService;

    private final StockService stockService;

    @KafkaListener(groupId = "orchestrator", topics = "stock.reduce.request")
    public void consumeStockMessage(StockDecreaseCommand command) {

        boolean success = stockService.validateAndDecrease(command.orderedProducts());

        if (success) {
            stockProducerService.sendStockDecreaseSuccess(
                new StockDecreaseSuccess(
                    command.orderId(),
                    command.orderedProducts()
                )
            );
        } else {
            stockProducerService.sendStockDecreaseFail(
                new StockDecreaseFail(command.orderId())
            );
        }
    }

    @KafkaListener(groupId = "orchestrator", topics = "stock.reduce.rollback")
    public void consumeStockRollbackMessage(StockRollbackCommand command) {
        stockService.rollbackQuantity(command.orderedProducts());
    }
}
