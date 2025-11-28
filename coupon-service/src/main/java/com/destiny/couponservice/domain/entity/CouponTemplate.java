package com.destiny.couponservice.domain.entity;

import com.destiny.couponservice.domain.enums.DiscountType;
import com.destiny.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "p_coupon_templates")
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
}
