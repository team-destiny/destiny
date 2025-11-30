package com.destiny.couponservice.domain.entity;

import com.destiny.couponservice.domain.enums.DiscountType;
import com.destiny.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "p_coupon_template")
public class CouponTemplate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DiscountType discountType;

    @Column(nullable = false)
    private Integer discountValue;

    @Column(nullable = false)
    private Integer minOrderAmount;

    @Column(nullable = false)
    private Boolean isDuplicateUsable;

    @Column
    private Integer maxDiscountAmount;

    @Column
    private Integer dailyIssueLimit;

    @Column
    private Integer perUserTotalLimit;

    @Column(nullable = false)
    private LocalDateTime availableFrom;

    @Column(nullable = false)
    private LocalDateTime availableTo;


    public void update(
        String name,
        DiscountType discountType,
        Integer discountValue,
        Integer minOrderAmount,
        Boolean isDuplicateUsable,
        Integer maxDiscountAmount,
        Integer dailyIssueLimit,
        Integer perUserTotalLimit,
        LocalDateTime availableFrom,
        LocalDateTime availableTo
    ) {
        if (name != null) {
            this.name = name;
        }
        if (discountType != null) {
            this.discountType = discountType;
        }
        if (discountValue != null) {
            this.discountValue = discountValue;
        }
        if (minOrderAmount != null) {
            this.minOrderAmount = minOrderAmount;
        }
        if (isDuplicateUsable != null) {
            this.isDuplicateUsable = isDuplicateUsable;
        }
        if (maxDiscountAmount != null) {
            this.maxDiscountAmount = maxDiscountAmount;
        }
        if (dailyIssueLimit != null) {
            this.dailyIssueLimit = dailyIssueLimit;
        }
        if (perUserTotalLimit != null) {
            this.perUserTotalLimit = perUserTotalLimit;
        }
        if (availableFrom != null) {
            this.availableFrom = availableFrom;
        }
        if (availableTo != null) {
            this.availableTo = availableTo;
        }
    }
}
