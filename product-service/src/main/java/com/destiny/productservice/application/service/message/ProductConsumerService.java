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

        Product product = productCommandRepository.findById(message.id())
            .orElseThrow(() -> new IllegalStateException(
                "쓰기 모델에 생성한 상품 " + message.id() + " 이 없습니다.")
            );

        try {
            productQueryRepository.save(ProductView.from(product));
        } catch (Exception e) {
            log.error("읽기 모델 상품 저장 실패 productId={} error={}",
                product.getId(),
                e.getMessage()
            );
            throw e;
        }
    }

    @KafkaListener(groupId = "product-group", topics = "product.after.update")
    @RetryableTopic(attempts = "3", backoff = @Backoff(delay = 1000, multiplier = 2))
    @Transactional
    public void consumeUpdateProductMessage(ProductMessage message) {

        log.info("productId : {} product update message consumed", message.id());

        ProductView productView = productQueryRepository.findById(message.id())
            .orElseThrow(() -> new IllegalStateException(
                "읽기 데이터베이스에 수정할 상품 " + message.id() + " 이 없습니다.")
            );

        try {
            productView.updateFrom(message);
            productQueryRepository.save(productView);
        } catch (Exception e) {
            log.error("읽기 모델 상품 수정 실패 productView.productId={}, error={}",
                productView.getId(),
                e.getMessage()
            );
            throw e;
        }
    }

    @KafkaListener(groupId = "product-group", topics = "product.after.delete")
    @RetryableTopic(attempts = "3", backoff = @Backoff(delay = 1000, multiplier = 2))
    @Transactional
    public void consumeDeleteProductMessage(ProductMessage message) {

        // TODO userId 파라미터 필요

    }
}
