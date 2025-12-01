package com.destiny.brandservice.domain.entity;

import com.destiny.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_brand")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Brand extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID brandId;

    @Column(nullable = false)
    private UUID managerId;

    @Column(nullable = false, length = 255)
    private String brandName;

    public void updateManagerId(UUID managerId) {
        this.managerId = managerId;
    }

    public void updateBrandName(String brandName) {
        this.brandName = brandName;
    }

    public static Brand of(UUID managerId, String brandName) {
        Brand brand = new Brand();
        brand.managerId = managerId;
        brand.brandName = brandName;
        return brand;
    }
}
