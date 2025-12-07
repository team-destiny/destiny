package com.destiny.productservice.domain.entity;

import static org.springframework.data.elasticsearch.annotations.FieldType.Text;

import com.destiny.productservice.application.dto.ProductMessage;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "product")
public class ProductView {

    @Id
    @Field(type = Text)
    private UUID id;

    private String name;

    private Integer price;

    @Field(type = Text)
    private UUID brandId;

    @Field(type = FieldType.Keyword)
    private ProductStatus status;

    private String color;

    private String size;

    public static ProductView from(Product product) {
        return new ProductView(
            product.getId(),
            product.getName(),
            product.getPrice(),
            product.getBrandId(),
            product.getStatus(),
            product.getColor(),
            product.getSize()
        );
    }

    public void updateFrom(ProductMessage message) {
        this.name = message.name();
        this.price = message.price();
        this.brandId = message.brandId();
        this.status = message.status();
        this.color = message.color();
        this.size = message.size();
    }
}
