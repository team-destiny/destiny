package com.destiny.paymentservice.infrastructure.repository;

import com.destiny.paymentservice.domain.entity.Payment;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentJpaRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByOrderId(UUID orderId);
    Optional<Payment> findByOrderIdAndUserId(UUID orderId, UUID userId);

    Page<Payment> findAllByUserId(UUID userId, Pageable pageable);
}
