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

    private final ProductQueryRepository productQueryRepository;

    @Override
    public Optional<ProductView> findById(UUID id) {
        return productQueryRepository.findById(id);
    }

    @Override
    public ProductView save(ProductView productView) {
        return productQueryRepository.save(productView);
    }

    @Override
    public void delete(ProductView productView) {
        productQueryRepository.delete(productView);
    }
}
