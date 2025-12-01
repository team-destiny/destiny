package com.destiny.productservice.infrastructure.repository.query;

import com.destiny.productservice.domain.entity.ProductView;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductQueryJpaRepository extends JpaRepository<ProductView, UUID> {

}
