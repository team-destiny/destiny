package com.destiny.stockservice.application.service.message;

import com.destiny.stockservice.application.dto.StockReduceFailResult;
import com.destiny.stockservice.application.dto.StockReduceSuccessResult;
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
    public void sendStockReduceSuccess(StockReduceSuccessResult stockReduceSuccessResult) {
        String message = objectMapper.writeValueAsString(stockReduceSuccessResult);
        kafkaTemplate.send("stock-reduce-success", message);
    }

    @SneakyThrows
    public void sendStockReduceFail(StockReduceFailResult stockReduceFailResult) {
        String message = objectMapper.writeValueAsString(stockReduceFailResult);
        kafkaTemplate.send("stock-reduce-fail", message);
    }
}
