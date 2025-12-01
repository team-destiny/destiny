package com.destiny.cartservice.domain.repository;

import com.destiny.cartservice.domain.model.Cart;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartRepository {

    Cart save(Cart cart);

    Optional<Cart> findById(UUID cartId);

    List<Cart> findAllByUserId(UUID userId);

    void deleteAllByIdIn(List<UUID> cartIds);

    // 장바구니 담기 시 동일한 상품/옵션이 있으면 수량을 합치기 위해 조회
    Optional<Cart> findExistingCart(UUID userId, UUID productId, UUID optionId);
}
