package com.destiny.cartservice.infrastructure.repository;

import com.destiny.cartservice.domain.model.Cart;
import com.destiny.cartservice.domain.repository.CartRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CartRepositoryImpl implements CartRepository {

    private final CartJpaRepository cartJpaRepository;


    @Override
    public Cart save(Cart cart) {
        return cartJpaRepository.save(cart);
    }

    @Override
    public Optional<Cart> findById(UUID cartId) {
        return cartJpaRepository.findById(cartId);
    }

    @Override
    public List<Cart> findAllByUserId(UUID userId) {
        return cartJpaRepository.findAllByUserId(userId);
    }

    @Override
    public void deleteAllByIdIn(List<UUID> cartIds) {
        cartJpaRepository.deleteAllByIdIn(cartIds);
    }

    @Override
    public Optional<Cart> findExistingCart(UUID userId, UUID productId, UUID optionId) {
        return cartJpaRepository.findByUserIdAndProductIdAndOptionId(userId, productId, optionId);
    }
}
