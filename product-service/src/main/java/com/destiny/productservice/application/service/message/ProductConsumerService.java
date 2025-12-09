package com.destiny.productservice.application.service.message;

import com.destiny.productservice.application.dto.ProductFailDetail;
import com.destiny.productservice.application.dto.ProductMessage;
import com.destiny.productservice.application.dto.ProductValidationCommand;
import com.destiny.productservice.application.dto.ProductValidationFail;
import com.destiny.productservice.application.dto.ProductValidationMessage;
import com.destiny.productservice.application.dto.ProductValidationSuccess;
import com.destiny.productservice.domain.entity.Product;
import com.destiny.productservice.domain.entity.ProductStatus;
import com.destiny.productservice.domain.entity.ProductView;
import com.destiny.productservice.domain.repository.ProductCommandRepository;
import com.destiny.productservice.domain.repository.ProductQueryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.RetryTopicHeaders;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductConsumerService {

    private final ProductCommandRepository productCommandRepository;
    private final ProductQueryRepository productQueryRepository;
    private final ProductProducerService productProducerService;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @KafkaListener(groupId = "product-group", topics = "product.after.create")
    @RetryableTopic(backoff = @Backoff(delay = 1000, multiplier = 2))
    @Transactional
    public void consumeCreateProductMessage(String productMessage,
        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
        @Header(name = RetryTopicHeaders.DEFAULT_HEADER_ATTEMPTS, required = false) Integer attempt
    ) {

        ProductMessage message = objectMapper
            .readValue(productMessage, ProductMessage.class);

        log.info("상품 생성 메세지를 소비했습니다. productId={} topic={}, attempt={}",
            message.id(),
            topic,
            attempt
        );

        Product product = productCommandRepository.findById(message.id())
            .orElseThrow(() -> new IllegalStateException(
                "쓰기 모델에 생성한 상품 " + message.id() + " 이 없습니다.")
            );

        try {
            productQueryRepository.save(ProductView.from(product));
        } catch (Exception e) {
            log.error("읽기 모델 상품 저장에 실패했습니다. attempt={}, productId={}, error={}",
                attempt,
                product.getId(),
                e.getMessage()
            );
            throw e;
        }
    }

    @SneakyThrows
    @KafkaListener(groupId = "product-group", topics = "product.after.update")
    @RetryableTopic(backoff = @Backoff(delay = 1000, multiplier = 2))
    @Transactional
    public void consumeUpdateProductMessage(String productMessage,
        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
        @Header(name = RetryTopicHeaders.DEFAULT_HEADER_ATTEMPTS, required = false) Integer attempt
    ) {

        ProductMessage message = objectMapper
            .readValue(productMessage, ProductMessage.class);

        log.info("상품 수정 메세지를 소비했습니다. productId = {} topic = {}, attempt = {}",
            message.id(),
            topic,
            attempt
        );

        ProductView productView = productQueryRepository.findById(message.id())
            .orElseThrow(() -> new IllegalStateException(
                "읽기 모델에 수정할 상품 " + message.id() + " 이 없습니다.")
            );

        try {
            productView.updateFrom(message);
            productQueryRepository.save(productView);
        } catch (Exception e) {
            log.error("읽기 모델 상품 수정에 실패했습니다. attempt = {}, productViewId = {}, error = {}",
                attempt,
                productView.getId(),
                e.getMessage()
            );
            throw e;
        }
    }

    @KafkaListener(groupId = "product-group", topics = "product.after.delete")
    @RetryableTopic(backoff = @Backoff(delay = 1000, multiplier = 2))
    @Transactional
    public void consumeDeleteProductMessage(ProductMessage message) {

        // TODO userId 파라미터 필요

    }

    @SneakyThrows
    @KafkaListener(groupId= "product-group", topics = "product-validate-request")
    @RetryableTopic(backoff = @Backoff(delay = 1000, multiplier = 2))
    @Transactional(readOnly = true)
    public void consumeProductValidateRequest(String message) {

        ProductValidationCommand event = objectMapper.readValue(message,
            ProductValidationCommand.class);

        List<UUID> productIds = event.productIds();

        List<Product> availableProducts = getAvailableProducts(productIds);

        if (availableProducts.size() == productIds.size()) {
            handleValidationSuccess(event.orderId(), availableProducts);
            return;
        }

        handleValidationFail(event.orderId(), productIds, availableProducts);

    }

    private List<Product> getAvailableProducts(List<UUID> productIds) {
        return productCommandRepository.findByIdInAndStatus(productIds, ProductStatus.AVAILABLE);
    }

    private void handleValidationSuccess(UUID orderId, List<Product> availableProducts) {

        List<ProductValidationMessage> messages = availableProducts.stream()
            .map(ProductValidationMessage::from)
            .toList();

        ProductValidationSuccess successMessage = new ProductValidationSuccess(orderId, messages);

        productProducerService.sendProductValidationSuccess(successMessage);
    }

    private void handleValidationFail(
        UUID orderId,
        List<UUID> productIds,
        List<Product> availableProducts
    ) {

        List<Product> allProducts = productCommandRepository.findByIdIn(productIds);

        Map<UUID, Product> productMap = allProducts.stream()
            .collect(Collectors.toMap(Product::getId, p -> p));

        Set<UUID> availableIds = availableProducts.stream()
            .map(Product::getId)
            .collect(Collectors.toSet());

        List<ProductFailDetail> failDetails = productIds.stream()
            .filter(id -> !availableIds.contains(id))
            .map(id -> mapFailDetail(id, productMap))
            .toList();

        productProducerService.sendProductValidationFail(
            new ProductValidationFail(orderId, failDetails)
        );
    }

    private ProductFailDetail mapFailDetail(UUID productId, Map<UUID, Product> productMap) {

        Product product = productMap.get(productId);

        if (product == null) {
            return new ProductFailDetail(productId, "NOT_FOUND");
        }

        return new ProductFailDetail(productId, "UNAVAILABLE: " + product.getStatus());
    }
}
