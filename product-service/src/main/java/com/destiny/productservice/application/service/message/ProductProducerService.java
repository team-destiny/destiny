package com.destiny.productservice.application.service.message;

import com.destiny.productservice.application.dto.ProductMessage;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductProducerService {

    private final KafkaTemplate<UUID, ProductMessage> kafkaTemplate;

    public void sendProductCreateAfterEvent(UUID key, ProductMessage productMessage) {
        kafkaTemplate.send("product_after_create", key, productMessage);
    }

    public void sendProductUpdateAfterEvent(UUID key, ProductMessage productMessage) {
        kafkaTemplate.send("product_after_update", key, productMessage);
    }

    public void sendProductDeleteAfterEvent(UUID key, ProductMessage productMessage) {
        kafkaTemplate.send("product_after_delete", key, productMessage);
    }
}
