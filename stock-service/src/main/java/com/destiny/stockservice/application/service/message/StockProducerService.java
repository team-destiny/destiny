package com.destiny.stockservice.application.service.message;

import com.destiny.stockservice.application.dto.StockReduceFail;
import com.destiny.stockservice.application.dto.StockReduceSuccess;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public void sendStockReduceSuccess(StockReduceSuccess stockReduceSuccess) {
        String message = objectMapper.writeValueAsString(stockReduceSuccess);
        kafkaTemplate.send("stock-reduce-success", message);
    }

    @SneakyThrows
    public void sendStockReduceFail(StockReduceFail stockReduceFail) {
        String message = objectMapper.writeValueAsString(stockReduceFail);
        kafkaTemplate.send("stock-decrease-fail", message);
    }
}
