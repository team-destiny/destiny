package com.destiny.productservice.application.service.message;

import com.destiny.productservice.application.dto.ProductFailDetail;
import com.destiny.productservice.application.dto.ProductValidationCommand;
import com.destiny.productservice.application.dto.ProductValidationFail;
import com.destiny.productservice.application.dto.ProductValidationMessage;
import com.destiny.productservice.application.dto.ProductValidationSuccess;
import com.destiny.productservice.domain.entity.Product;
import com.destiny.productservice.domain.entity.ProductStatus;
import com.destiny.productservice.domain.repository.ProductCommandRepository;
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
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductValidationService {

    private final ProductCommandRepository productCommandRepository;
    private final ProductProducerService productProducerService;
    private final ObjectMapper objectMapper;

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
        return productCommandRepository
            .findByIdInAndStatus(productIds, ProductStatus.AVAILABLE);
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
