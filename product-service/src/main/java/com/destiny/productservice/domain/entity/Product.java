package com.destiny.productservice.domain.entity;

import com.destiny.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_product")
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private Integer price;

    private UUID brandId;

    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    private String color;

    private String size;

    public static Product of(String name, Integer price, UUID brandId, String color, String size) {
        return new Product(
            null, name, price, brandId, ProductStatus.AVAILABLE, color, size
        );
    }

    // Product와 ProductView는 CQRS 구조상 동일 필드 업데이트 로직을 가져 중복을 제거하지 않았습니다.
    @SuppressWarnings("DuplicatedCode")
    public void update(
        String name, Integer price, ProductStatus status, UUID brandId, String color, String size
    ) {
        if (name != null) {
            this.name = name;
        }

        if (price != null) {
            this.price = price;
        }

        if (brandId != null) {
            this.brandId = brandId;
        }

        if (status != null) {
            this.status = status;
        }

        if (color != null) {
            this.color = color;
        }

        if (size != null) {
            this.size = size;
        }
    }

    public void markOutOfStock() {
        this.status = ProductStatus.OUT_OF_STOCK;
    }

    public void markAvailable() {
        this.status = ProductStatus.AVAILABLE;
    }

    public void markHided() {
        this.status = ProductStatus.HIDED;
    }
}
