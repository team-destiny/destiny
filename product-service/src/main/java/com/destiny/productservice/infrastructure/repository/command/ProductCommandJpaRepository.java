package com.destiny.productservice.infrastructure.repository.command;

import com.destiny.productservice.domain.entity.Product;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCommandJpaRepository extends JpaRepository<Product, UUID> {
    boolean existsByNameAndBrand(String name, String brand);
}
