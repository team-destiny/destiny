package com.destiny.productservice.application.service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.json.JsonData;
import com.destiny.productservice.application.dto.ProductSearch;
import com.destiny.productservice.domain.entity.ProductView;
import com.destiny.productservice.domain.repository.ProductQueryRepository;
import com.destiny.productservice.presentation.dto.response.ProductResponse;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductQueryService {

    private final ProductQueryRepository productQueryRepository;

    private final ElasticsearchOperations elasticsearchOperations;

    public ProductResponse getProductById(UUID productId) {

        ProductView productView = productQueryRepository.findById(productId)
            .orElseThrow();

        return ProductResponse.of(productView);
    }

    public Page<ProductResponse> getProducts(ProductSearch search, Pageable pageable) {

        NativeQuery query = buildSearchQuery(search, pageable);

        SearchHits<ProductView> hits = elasticsearchOperations.search(query, ProductView.class);

        return toPage(hits, pageable).map(ProductResponse::of);
    }

    private NativeQuery buildSearchQuery(ProductSearch search, Pageable pageable) {

        BoolQuery.Builder bool = QueryBuilders.bool();

        addPriceFilter(search.minPrice(), search.maxPrice(), bool);
        addNameContainsCondition(search.nameContains(), bool);
        addBrandFilter(search.brandId().toString(), bool);
        addSizeFilter(search.size(), bool);
        addColorFilter(search.color(), bool);

        return NativeQuery.builder()
            .withQuery(bool.build()._toQuery())
            .withPageable(pageable)
            .build();
    }

    private Page<ProductView> toPage(SearchHits<ProductView> hits, Pageable pageable) {

        List<ProductView> content = hits.getSearchHits()
            .stream()
            .map(SearchHit::getContent)
            .toList();

        long total = hits.getTotalHits();

        return new PageImpl<>(content, pageable, total);
    }

    private void addColorFilter(String color, BoolQuery.Builder boolQuery) {
        if (color != null && !color.isBlank()) {
            boolQuery.filter(
                QueryBuilders.term()
                    .field("color.keyword")
                    .value(color)
                    .build()
                    ._toQuery()
            );
        }
    }

    private void addSizeFilter(String size, BoolQuery.Builder boolQuery) {
        if (size != null && !size.isBlank()) {
            boolQuery.filter(
                QueryBuilders.term()
                    .field("size.keyword")
                    .value(size)
                    .build()
                    ._toQuery()
            );
        }
    }

    private void addBrandFilter(String brand, BoolQuery.Builder boolQuery) {
        if (brand != null && !brand.isBlank()) {
            boolQuery.filter(
                QueryBuilders.term()
                    .field("brand.keyword")
                    .value(brand)
                    .build()
                    ._toQuery()
            );
        }
    }

    private void addNameContainsCondition(String nameContains, BoolQuery.Builder boolQuery) {
        if (nameContains != null && !nameContains.isBlank()) {

            String keyword = "*%s*".formatted(nameContains);

            boolQuery.must(
                QueryBuilders.queryString()
                    .fields("name")
                    .query(keyword)
                    .build()
                    ._toQuery()
            );
        }
    }

    private void addPriceFilter(Integer minPrice, Integer maxPrice, BoolQuery.Builder boolQuery) {
        if (minPrice != null || maxPrice != null) {

            RangeQuery rangeQuery = new RangeQuery.Builder()
                .untyped(u -> {
                    u.field("price");

                    if (minPrice != null) {
                        u.gte(JsonData.of(minPrice));
                    }
                    if (maxPrice != null) {
                        u.lte(JsonData.of(maxPrice));
                    }
                    return u;
                })
                .build();

            boolQuery.filter(rangeQuery._toQuery());
        }
    }
}