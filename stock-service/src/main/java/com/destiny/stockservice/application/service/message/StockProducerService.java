package com.destiny.stockservice.application.service.message;

import com.destiny.stockservice.application.dto.StockDecreaseFail;
import com.destiny.stockservice.application.dto.StockDecreaseSuccess;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendStockDecreaseSuccess(StockDecreaseSuccess message) {
        kafkaTemplate.send("stock-reduce-success", message);
    }

    public void sendStockDecreaseFail(StockDecreaseFail message) {
        kafkaTemplate.send("stock-decrease-fail", message);
    }
}
