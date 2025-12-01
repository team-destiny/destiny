package com.destiny.productservice.application.service;

import static java.util.stream.Collectors.toList;

import com.destiny.productservice.application.command.CreateProductCommand;
import com.destiny.productservice.application.command.UpdateProductCommand;
import com.destiny.productservice.application.dto.ProductMessage;
import com.destiny.productservice.application.service.message.ProductProducerService;
import com.destiny.productservice.domain.entity.Product;
import com.destiny.productservice.domain.repository.ProductCommandRepository;
import com.destiny.productservice.presentation.dto.response.ProductResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
public class ProductCommandService {

    private final ProductCommandRepository productCommandRepository;

    private final ProductProducerService productProducerService;

    public ProductResponse createProduct(CreateProductCommand command) {

        // TODO 예외 처리

        Product product = command.toEntity();

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

    public void updateProduct(UpdateProductCommand command) {

        Product product = productCommandRepository.findById(command.id()).orElseThrow();

    }
}
