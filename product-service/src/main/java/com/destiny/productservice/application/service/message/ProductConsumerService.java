package com.destiny.productservice.application.service.message;

import com.destiny.productservice.application.dto.ProductMessage;
import com.destiny.productservice.domain.entity.Product;
import com.destiny.productservice.domain.entity.ProductView;
import com.destiny.productservice.domain.repository.ProductCommandRepository;
import com.destiny.productservice.domain.repository.ProductQueryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductConsumerService {

    private final ProductCommandRepository productCommandRepository;
    private final ProductQueryRepository productQueryRepository;

    @KafkaListener(groupId = "product", topics = "product_after_create")
    @RetryableTopic(attempts = "3", backoff = @Backoff(delay = 1000, multiplier = 2))
    @Transactional
    public void consumeCreateProductMessage(ProductMessage productMessage) {

        log.info("productId : {} product create message consumed", productMessage.id());

        Product product = productCommandRepository.findById(productMessage.id()).orElseThrow();

        try {
            productQueryRepository.save(ProductView.from(product));
        } catch (Exception e) {
            log.error("{} product creation rollback. {}", product.getId(), e.getMessage());

            productQueryRepository.delete(ProductView.from(product));
        }
    }

    @KafkaListener(groupId = "product", topics = "product_after_update")
    @RetryableTopic(attempts = "3", backoff = @Backoff(delay = 1000, multiplier = 2))
    @Transactional
    public void consumeUpdateProductMessage(ProductMessage message) {

        log.info("productId : {} product update message consumed", message.id());

        ProductView productView = productQueryRepository.findById(message.id()).orElseThrow();

        try {
            productView.updateFrom(message);
            productQueryRepository.save(productView);
        } catch (Exception e) {
            log.error("{} product update failed. {}", productView.getId(), e.getMessage());
        }
    }
}
