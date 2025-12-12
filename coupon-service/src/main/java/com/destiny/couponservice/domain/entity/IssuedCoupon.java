package com.destiny.couponservice.domain.entity;

import com.destiny.couponservice.domain.enums.IssuedCouponStatus;
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
@Table(name = "p_issued_coupon")
public class IssuedCoupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID couponTemplateId;

    @Column
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private IssuedCouponStatus status;

    @Column(nullable = false)
    private LocalDateTime issuedAt;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    @Column
    private LocalDateTime usedAt;


    public void use(UUID orderId, LocalDateTime usedAt) {
        this.orderId = orderId;
        this.usedAt = usedAt;
        this.status = IssuedCouponStatus.USED;
    }


    public void expire(LocalDateTime now) {
        this.status = IssuedCouponStatus.EXPIRED;
        this.expiredAt = now;
    }

    public boolean isUsable(LocalDateTime now) {
        return this.status == IssuedCouponStatus.AVAILABLE
            && now.isBefore(this.expiredAt);
    }

    public void cancelUse() {
        this.status = IssuedCouponStatus.AVAILABLE;
        this.orderId = null;
        this.usedAt = null;
    }

    public void updateStatus(IssuedCouponStatus status, LocalDateTime usedAt) {
        this.status = status;
        this.usedAt = usedAt;

    }

}
