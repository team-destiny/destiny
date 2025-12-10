package com.destiny.productservice.domain.entity;

import com.destiny.global.entity.BaseEntity;
import com.destiny.productservice.presentation.dto.request.UpdateProductRequest;
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

    public void update(UpdateProductRequest request) {
        if (request.name() != null) {
            this.name = request.name();
        }

        if (request.price() != null) {
            this.price = request.price();
        }

        if (request.status() != null) {
            this.status = request.status();
        }

        if (request.color() != null) {
            this.color = request.color();
        }

        if (request.size() != null) {
            this.size = request.size();
        }
    }
}
