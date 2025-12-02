package com.destiny.productservice.infrastructure.repository.query;

import com.destiny.productservice.domain.entity.ProductView;
import com.destiny.productservice.presentation.dto.request.ProductSearch;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductQueryJpaRepository extends JpaRepository<ProductView, UUID> {

    @Query("""
    SELECT p FROM ProductView p
    WHERE (:#{#s.nameContains} IS NULL OR p.name LIKE %:#{#s.nameContains}%)
      AND (:#{#s.color} IS NULL OR p.color = :#{#s.color})
      AND (:#{#s.size} IS NULL OR p.size = :#{#s.size})
      AND (:#{#s.minPrice} IS NULL OR p.price >= :#{#s.minPrice})
      AND (:#{#s.maxPrice} IS NULL OR p.price <= :#{#s.maxPrice})
    """)
    Page<ProductView> search(@Param("s")ProductSearch productSearch, Pageable pageable);
}
