package com.destiny.productservice.application.service.message;

import com.destiny.productservice.application.dto.ProductMessage;
import com.destiny.productservice.application.dto.ProductValidationFail;
import com.destiny.productservice.application.dto.ProductValidationSuccess;
import com.destiny.productservice.application.dto.StockCreateMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    @SneakyThrows
    public void sendProductCreateAfterEvent(String key, ProductMessage message) {
        kafkaTemplate.send(
            "product.after.create",
            key,
            objectMapper.writeValueAsString(message)
        );
    }

    @SneakyThrows
    public void sendProductUpdateAfterEvent(String key, ProductMessage message) {
        kafkaTemplate.send(
            "product.after.update",
            key,
            objectMapper.writeValueAsString(message)
        );
    }

    @SneakyThrows
    public void sendProductDeleteAfterEvent(String key, ProductMessage message) {
        kafkaTemplate.send(
            "product.after.delete",
            key,
            objectMapper.writeValueAsString(message)
        );
    }

    @SneakyThrows
    public void sendProductValidationSuccess(ProductValidationSuccess message) {
        kafkaTemplate.send(
            "product-validate-success",
            objectMapper.writeValueAsString(message)
        );
    }

    @SneakyThrows
    public void sendProductValidationFail(ProductValidationFail message) {
        kafkaTemplate.send(
            "product-validate-fail",
            objectMapper.writeValueAsString(message)
        );
    }

    @SneakyThrows
    public void sendProductStock(StockCreateMessage stockCreateMessage) {
        kafkaTemplate.send(
            "stock-create-message",
            objectMapper.writeValueAsString(stockCreateMessage)
        );
    }
}
