package com.destiny.productservice.domain.entity;

import jakarta.persistence.Entity;
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
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private Long price;

    private String brand;

    private ProductStatus status;

    private String color;

    private String size;

    public static Product create(String name, Long price, String brand, String color, String size) {
        return new Product(
            null, name, price, brand, ProductStatus.AVAILABLE, color, size
        );
    }

    public static ProductView createView(Product product) {
        return new ProductView(
            product.id,
            product.name,
            product.price,
            product.brand,
            product.status,
            product.color,
            product.size
        );
    }
}
