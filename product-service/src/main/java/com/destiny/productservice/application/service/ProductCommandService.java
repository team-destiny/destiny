package com.destiny.productservice.application.service;

import com.destiny.productservice.application.dto.ProductMessage;
import com.destiny.productservice.application.service.message.ProductProducerService;
import com.destiny.productservice.domain.entity.Product;
import com.destiny.productservice.domain.repository.ProductCommandRepository;
import com.destiny.productservice.presentation.dto.request.CreateProductRequest;
import com.destiny.productservice.presentation.dto.request.UpdateProductRequest;
import com.destiny.productservice.presentation.dto.response.ProductResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
public class ProductCommandService {

    private final ProductCommandRepository productCommandRepository;

    private final ProductProducerService productProducerService;

    public ProductResponse createProduct(CreateProductRequest request) {

        if (productCommandRepository.existsByNameAndBrand(request.name(), request.brand())) {
            throw new IllegalArgumentException();
        }

        Product product = Product.of(
            request.name(),
            request.price(),
            request.brand(),
            request.color(),
            request.size()
        );

        productCommandRepository.save(product);

        TransactionSynchronizationManager.registerSynchronization(
            new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    productProducerService.sendProductCreateAfterEvent(
                        product.getId(),
                        ProductMessage.from(product)
                    );
                }
            }
        );

        return ProductResponse.of(product);
    }

    public void updateProduct(UpdateProductRequest request) {

        Product product = productCommandRepository.findById(request.id()).orElseThrow();

        product.update(
            request.name(),
            request.price(),
            request.brand(),
            request.status(),
            request.color(),
            request.size()
        );

        TransactionSynchronizationManager.registerSynchronization(
            new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    productProducerService.sendProductUpdateAfterEvent(
                        product.getId(),
                        ProductMessage.from(product)
                    );
                }
            }
        );
    }

    public void deleteProduct(UUID productId) {

        Product product = productCommandRepository.findById(productId).orElseThrow();

        // TODO UserDetails 파라미터 필요, 소프트 딜리트 처리

        TransactionSynchronizationManager.registerSynchronization(
            new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    productProducerService.sendProductDeleteAfterEvent(
                        product.getId(),
                        ProductMessage.from(product)
                    );
                }
            }
        );
    }
}
