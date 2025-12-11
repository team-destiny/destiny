package com.destiny.productservice.domain.entity;

import static org.springframework.data.elasticsearch.annotations.FieldType.Text;

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

    // Product와 ProductView는 CQRS 구조상 동일 필드 업데이트 로직을 가져 중복을 제거하지 않았습니다.
    @SuppressWarnings("DuplicatedCode")
    public void update(String name, Integer price, ProductStatus status, String color, String size) {
        if (name != null) {
            this.name = name;
        }

        if (price != null) {
            this.price = price;
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
}
