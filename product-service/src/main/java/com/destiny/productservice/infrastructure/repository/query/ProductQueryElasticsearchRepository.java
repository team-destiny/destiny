package com.destiny.productservice.infrastructure.repository.query;

import com.destiny.productservice.domain.entity.ProductView;
import java.util.UUID;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductQueryElasticsearchRepository extends
    ElasticsearchRepository<ProductView, UUID> {
}
