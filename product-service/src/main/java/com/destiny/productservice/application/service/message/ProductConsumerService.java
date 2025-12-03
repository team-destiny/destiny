package com.destiny.productservice.application.service.message;

import com.destiny.productservice.application.dto.ProductMessage;
import com.destiny.productservice.domain.entity.Product;
import com.destiny.productservice.domain.entity.ProductView;
import com.destiny.productservice.domain.repository.ProductCommandRepository;
import com.destiny.productservice.domain.repository.ProductQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductConsumerService {

    private final ProductCommandRepository productCommandRepository;
    private final ProductQueryRepository productQueryRepository;

    @KafkaListener(groupId = "product-group", topics = "product.after.create")
    @RetryableTopic(attempts = "3", backoff = @Backoff(delay = 1000, multiplier = 2))
    @Transactional
    public void consumeCreateProductMessage(ProductMessage message) {

        log.info("productId : {} product create message consumed", message.id());

        Product product = productCommandRepository.findById(message.id()).orElseThrow();

        try {
            productQueryRepository.save(ProductView.from(product));
        } catch (Exception e) {
            log.error("{} product creation rollback. {}", product.getId(), e.getMessage());

            productCommandRepository.deleteById(message.id());
        }
    }

    @KafkaListener(groupId = "product-group", topics = "product.after.update")
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

    @KafkaListener(groupId = "product-group", topics = "product.after.delete")
    @RetryableTopic(attempts = "3", backoff = @Backoff(delay = 1000, multiplier = 2))
    @Transactional
    public void consumeDeleteProductMessage(ProductMessage message) {

        // TODO userId 파라미터 필요

    }
}
