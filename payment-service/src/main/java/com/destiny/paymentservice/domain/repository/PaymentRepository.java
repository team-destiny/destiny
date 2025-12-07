package com.destiny.paymentservice.domain.repository;

import com.destiny.paymentservice.domain.entity.Payment;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findByOrderId(UUID orderId);
    Optional<Payment> findByOrderIdAndUserId(UUID orderId, UUID userId);
    Page<Payment> findAll(Pageable pageable);
    Page<Payment> findAllByUserId(UUID userId, Pageable pageable);
}