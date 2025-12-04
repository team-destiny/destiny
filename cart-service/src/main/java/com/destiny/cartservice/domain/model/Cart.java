package com.destiny.cartservice.domain.model;

import com.destiny.global.entity.BaseEntity;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_cart")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // 회원만 저장
    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID productId;

    @Column
    private UUID optionId; // null 가능

    @Column(nullable = false)
    private int quantity;

    public static Cart of(UUID userId, UUID productId, UUID optionId, int quantity) {
        Cart cart = new Cart();
        cart.userId = userId;
        cart.productId = productId;
        cart.optionId = optionId;
        cart.quantity = quantity;
        return cart;
    }

    // 수량 업데이트
    public void updateQuantity(int quantity) {
        this.quantity = quantity;
    }

}
