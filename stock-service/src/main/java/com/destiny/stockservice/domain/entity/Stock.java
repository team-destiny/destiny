package com.destiny.stockservice.domain.entity;

import com.destiny.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Stock extends BaseEntity {

    @Id
    private UUID id;

    private UUID productId;

    @Column(nullable = false)
    private Integer quantity;

    @Version
    private Long version;

    public void tryReduceQuantity(Integer amount) {
        if (quantity < amount) {
            return;
        }
        quantity -= amount;
    }

    public void addQuantity(Integer amount) {
        quantity += amount;
    }
}
