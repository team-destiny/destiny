package com.destiny.productservice.domain.repository;

import com.destiny.productservice.domain.entity.Product;
import java.util.Optional;
import java.util.UUID;

public interface ProductCommandRepository {
    Optional<Product> findById(UUID id);
    Product save(Product product);
    void deleteById(UUID id);
}
