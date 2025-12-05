package com.destiny.cartservice.infrastructure.repository;

import com.destiny.cartservice.domain.model.Cart;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartJpaRepository extends JpaRepository<Cart, UUID> {

    List<Cart> findAllByUserId(UUID userId);

    void deleteAllByIdIn(List<UUID> cartIds);

    Optional<Cart> findByUserIdAndProductIdAndOptionId(UUID userId, UUID productId, UUID optionId);

    void deleteAllByUserId(UUID userId);

}
