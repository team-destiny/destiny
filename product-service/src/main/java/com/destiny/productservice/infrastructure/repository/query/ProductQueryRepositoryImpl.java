package com.destiny.productservice.infrastructure.repository.query;

import com.destiny.productservice.domain.entity.ProductView;
import com.destiny.productservice.domain.repository.ProductQueryRepository;
import com.destiny.productservice.presentation.dto.request.ProductSearch;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductQueryRepositoryImpl implements ProductQueryRepository {

    private final ProductQueryJpaRepository jpaRepository;

    @Override
    public Optional<ProductView> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public ProductView save(ProductView productView) {
        return jpaRepository.save(productView);
    }

    @Override
    public void delete(ProductView productView) {
        jpaRepository.delete(productView);
    }

    @Override
    public Page<ProductView> search(ProductSearch productSearch, Pageable pageable) {
        return jpaRepository.search(productSearch, pageable);
    }
}
