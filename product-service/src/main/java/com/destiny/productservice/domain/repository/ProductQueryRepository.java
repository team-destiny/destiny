package com.destiny.productservice.domain.repository;

import com.destiny.productservice.domain.entity.ProductView;
import java.util.Optional;
import java.util.UUID;

public interface ProductQueryRepository {
    Optional<ProductView> findById(UUID id);
    ProductView save(ProductView productView);
}
