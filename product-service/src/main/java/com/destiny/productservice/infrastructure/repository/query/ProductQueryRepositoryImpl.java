package com.destiny.productservice.infrastructure.repository.query;

import com.destiny.productservice.domain.entity.ProductView;
import com.destiny.productservice.domain.repository.ProductQueryRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductQueryRepositoryImpl implements ProductQueryRepository {

    private final ProductQueryElasticsearchRepository elasticsearchRepository;

    @Override
    public Optional<ProductView> findById(UUID id) {
        return elasticsearchRepository.findById(id);
    }

    @Override
    public ProductView save(ProductView productView) {
        return elasticsearchRepository.save(productView);
    }

    @Override
    public Optional<ProductView> findByBrandIdAndId(UUID brandId, UUID productId) {
        return elasticsearchRepository.findByBrandIdAndId(brandId, productId);
    }
}
