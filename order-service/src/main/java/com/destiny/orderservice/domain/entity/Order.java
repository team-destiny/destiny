package com.destiny.orderservice.domain.entity;

import com.destiny.global.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_order")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID orderId;
    private UUID userId;
    private UUID couponId;
    private Integer originalAmount;
    private Integer finalAmount;
    private Integer discountAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private String paymentMethod;
    private String recipientName;
    private String recipientPhone;
    private String zipcode;
    private String address1;
    private String address2;
    private String deliveryMessage;
    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST)
    private List<OrderItem> items = new ArrayList<>();

    private String failureReason;

    public void updateAmounts(
        Integer originalAmount,
        Integer discountAmount,
        Integer finalAmount
    ) {
        this.originalAmount = originalAmount;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
    }

    public void markCompleted() {
        this.orderStatus = OrderStatus.COMPLETED;
    }

    public void updateStatus(OrderStatus status) {
        this.orderStatus = status;
    }

    public void updateFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public static Order of(
        UUID userId,
        UUID couponId,
        String paymentMethod,
        String recipientName,
        String recipientPhone,
        String zipcode,
        String address1,
        String address2,
        String deliveryMessage
    ) {
        Order order = new Order();
        order.userId = userId;
        order.couponId = couponId;
        order.originalAmount = null;
        order.finalAmount = null;
        order.discountAmount = null;
        order.orderStatus = OrderStatus.PENDING;
        order.paymentMethod = paymentMethod;
        order.recipientName = recipientName;
        order.recipientPhone = recipientPhone;
        order.zipcode = zipcode;
        order.address1 = address1;
        order.address2 = address2;
        order.deliveryMessage = deliveryMessage;

        return order;
    }

    public void addItem(OrderItem item) {
        items.add(item);
        item.addOrder(this);
    }

    public Optional<OrderItem> findItem(UUID productId) {
        return this.items.stream()
            .filter(i -> i.getProductId().equals(productId))
            .findFirst();
    }

    public void updateItem(
        OrderItem item,
        UUID brandId,
        Integer unitPrice,
        Integer finalPrice,
        Integer discountAmount,
        Integer stock
        ) {

        item.updateItemInfo(brandId, unitPrice, finalPrice, discountAmount, stock);
    }
}
