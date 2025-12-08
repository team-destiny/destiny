package com.destiny.productservice.domain.repository;

import com.destiny.productservice.domain.entity.Product;
import com.destiny.productservice.domain.entity.ProductStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductCommandRepository {
    Optional<Product> findById(UUID productId);
    Product save(Product product);
    void deleteById(UUID productId);
    boolean existsByBrandIdAndName(UUID brandId, String name);
    List<Product> findByIdInAndStatus(List<UUID> ids, ProductStatus status);
    List<Product> findByIdIn(List<UUID> productIds);
    Optional<Product> findByBrandIdAndId(UUID brandId, UUID productId);
}
