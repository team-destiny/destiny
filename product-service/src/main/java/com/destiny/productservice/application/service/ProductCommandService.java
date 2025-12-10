package com.destiny.productservice.application.service;

import com.destiny.productservice.application.dto.ProductMessage;
import com.destiny.productservice.application.service.message.ProductProducerService;
import com.destiny.productservice.application.service.message.StockCreateMessage;
import com.destiny.productservice.domain.entity.Product;
import com.destiny.productservice.domain.repository.ProductCommandRepository;
import com.destiny.productservice.presentation.dto.request.CreateProductRequest;
import com.destiny.productservice.presentation.dto.request.UpdateProductRequest;
import com.destiny.productservice.presentation.dto.response.ProductResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
public class ProductCommandService {

    private final ProductCommandRepository productCommandRepository;

    private final ProductProducerService productProducerService;

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {

        if (productCommandRepository.existsByBrandIdAndName(request.brandId(), request.name())) {
            throw new IllegalArgumentException();
        }

        Product product = Product.of(
            request.name(),
            request.price(),
            request.brandId(),
            request.color(),
            request.size()
        );

        productCommandRepository.save(product);

        TransactionSynchronizationManager.registerSynchronization(
            new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    productProducerService.sendProductCreateAfterEvent(
                        product.getId().toString(),
                        ProductMessage.from(product)
                    );

                    productProducerService.sendProductStock(
                        new StockCreateMessage(
                            product.getId(),
                            request.quantity()
                        )
                    );
                }
            }
        );

        return ProductResponse.of(product);
    }

    @Transactional
    public void updateProduct(UUID productId, UpdateProductRequest request) {

        Product product = productCommandRepository.findById(productId)
            .orElseThrow();

        product.update(request);

        TransactionSynchronizationManager.registerSynchronization(
            new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    productProducerService.sendProductUpdateAfterEvent(
                        product.getId().toString(),
                        ProductMessage.from(product)
                    );
                }
            }
        );
    }

    @Transactional
    public void deleteProduct(UUID productId) {

        Product product = productCommandRepository
            .findById(productId)
            .orElseThrow();

        TransactionSynchronizationManager.registerSynchronization(
            new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    productProducerService.sendProductDeleteAfterEvent(
                        product.getId().toString(),
                        ProductMessage.from(product)
                    );
                }
            }
        );
    }
}
