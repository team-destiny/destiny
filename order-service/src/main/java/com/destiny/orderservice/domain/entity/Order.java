package com.destiny.orderservice.domain.entity;

import com.destiny.global.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
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

    public static Order of(
        UUID userId,
        UUID couponId,
        Integer originalAmount,
        Integer finalAmount,
        Integer discountAmount,
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
        order.originalAmount = originalAmount;
        order.finalAmount = finalAmount;
        order.discountAmount = discountAmount;
        order.orderStatus = OrderStatus.CREATED;
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
}
