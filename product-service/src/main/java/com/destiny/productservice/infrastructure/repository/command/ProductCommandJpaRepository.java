package com.destiny.productservice.infrastructure.repository.command;

import com.destiny.productservice.domain.entity.Product;
import com.destiny.productservice.domain.entity.ProductStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCommandJpaRepository extends JpaRepository<Product, UUID> {

    boolean existsByNameAndBrandId(String name, UUID brandId);

    List<Product> findByIdInAndStatus(List<UUID> ids, ProductStatus status);

    List<Product> findByIdIn(List<UUID> productIds);
}
