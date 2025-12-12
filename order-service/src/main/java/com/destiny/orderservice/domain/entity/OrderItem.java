package com.destiny.orderservice.domain.entity;

import com.destiny.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_order_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID orderItemId;
    private UUID productId;
    private UUID itemPromotionId;
    private UUID brandId;
    private Integer stock;
    private Integer unitPrice;
    private Integer finalPrice;
    private Integer itemDiscountAmount;
    @Enumerated(EnumType.STRING)
    private OrderItemStatus status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    public void updateItemInfo(
        UUID brandId,
        Integer unitPrice,
        Integer finalPrice,
        Integer itemDiscountAmount,
        Integer stock
    ) {
        this.brandId = brandId;
        this.unitPrice = unitPrice;
        this.finalPrice = finalPrice;
        this.itemDiscountAmount = itemDiscountAmount;
        this.stock = stock;
    }


    public void updateStatus(OrderItemStatus status) {
        this.status = status;
    }

    public static OrderItem of(
        UUID productId,
        UUID itemPromotionId,
        Integer stock
    ) {
        OrderItem orderItem = new OrderItem();
        orderItem.productId = productId;
        orderItem.itemPromotionId = itemPromotionId;
        orderItem.stock = stock;
        orderItem.brandId = null;
        orderItem.unitPrice = null;
        orderItem.finalPrice = null;
        orderItem.itemDiscountAmount = null;
        orderItem.status = OrderItemStatus.PENDING;

        return orderItem;
    }

    public void addOrder(Order order) {
        this.order = order;
    }
}
