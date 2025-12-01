package com.destiny.productservice.domain.entity;

import com.destiny.productservice.application.dto.ProductMessage;
import jakarta.persistence.Entity;
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
@Table(name = "p_product_view")
public class ProductView {

    @Id
    private UUID id;

    private String name;

    private Long price;

    private String brand;

    private ProductStatus status;

    private String color;

    private String size;

    public static ProductView from(Product product) {
        return new ProductView(
            product.getId(),
            product.getName(),
            product.getPrice(),
            product.getBrand(),
            product.getStatus(),
            product.getColor(),
            product.getSize()
        );
    }

    public static ProductView from(ProductMessage message) {
        return new ProductView(
            message.id(),
            message.name(),
            message.price(),
            message.brand(),
            message.status(),
            message.color(),
            message.size()
        );
    }

    public void updateFrom(ProductMessage message) {
        this.name = message.name();
        this.price = message.price();
        this.brand = message.brand();
        this.status = message.status();
        this.color = message.color();
        this.size = message.size();
    }
}
