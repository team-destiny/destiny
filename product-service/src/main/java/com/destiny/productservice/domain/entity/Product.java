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

    public void update(String name, Integer price, UUID brandId, ProductStatus status, String color, String size) {
        this.name = name;
        this.price = price;
        this.brandId = brandId;
        this.status = status;
        this.color = color;
        this.size = size;
    }
}
