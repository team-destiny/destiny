package com.destiny.stockservice.application.service.message;

import com.destiny.stockservice.application.dto.StockReduceFail;
import com.destiny.stockservice.application.dto.StockReduceSuccess;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendStockReduceSuccess(StockReduceSuccess message) {
        kafkaTemplate.send("stock-reduce-success", message);
    }

    public void sendStockReduceFail(StockReduceFail message) {
        kafkaTemplate.send("stock-decrease-fail", message);
    }
}
