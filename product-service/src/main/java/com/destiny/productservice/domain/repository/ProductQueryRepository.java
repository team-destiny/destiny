package com.destiny.productservice.domain.repository;

import com.destiny.productservice.domain.entity.ProductView;
import com.destiny.productservice.presentation.dto.request.ProductSearch;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductQueryRepository {
    Optional<ProductView> findById(UUID id);
    ProductView save(ProductView productView);
    void delete(ProductView productView);
    Page<ProductView> search(ProductSearch productSearch, Pageable pageable);
}
