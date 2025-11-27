package com.gbg.brandservice.domain.entity;

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
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID brandId;

    private UUID managerId;

    private String brandName;

    /* TODO
        11. 27  | 공통 모듈 적용 안되는 이슈
                  이슈 해결되면 BaseEntity 상속 받아야 함.
     */

    public static Brand of(UUID managerId, String brandName) {
        Brand brand = new Brand();
        brand.brandId = managerId;
        brand.brandName = brandName;
        return brand;
    }
}
