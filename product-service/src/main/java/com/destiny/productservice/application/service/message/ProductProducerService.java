package com.destiny.productservice.application.service.message;

import com.destiny.productservice.application.dto.ProductMessage;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendProductCreateAfterEvent(String key, ProductMessage message) {
        kafkaTemplate.send("product.after.create", key, message);
    }

    public void sendProductUpdateAfterEvent(String key, ProductMessage message) {
        kafkaTemplate.send("product.after.update", key, message);
    }

    public void sendProductDeleteAfterEvent(String key, ProductMessage message) {
        kafkaTemplate.send("product.after.delete", key, message);
    }
}
