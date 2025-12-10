package com.destiny.productservice.infrastructure.repository.command;

import com.destiny.productservice.domain.entity.Product;
import com.destiny.productservice.domain.entity.ProductStatus;
import com.destiny.productservice.domain.repository.ProductCommandRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductCommandRepositoryImpl implements ProductCommandRepository {

    private final ProductCommandJpaRepository productCommandJpaRepository;

    @Override
    public Optional<Product> findById(UUID id) {
        return productCommandJpaRepository.findById(id);
    }

    @Override
    public Product save(Product product) {
        return productCommandJpaRepository.save(product);
    }

    @Override
    public void deleteById(UUID id) {
        productCommandJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByBrandIdAndName(UUID brandId, String name) {
        return productCommandJpaRepository.existsByBrandIdAndName(brandId, name);
    }

    @Override
    public List<Product> findByIdInAndStatus(List<UUID> ids, ProductStatus status) {
        return productCommandJpaRepository.findByIdInAndStatus(ids, status);
    }

    @Override
    public List<Product> findByIdIn(List<UUID> productIds) {
        return productCommandJpaRepository.findByIdIn(productIds);
    }
}
