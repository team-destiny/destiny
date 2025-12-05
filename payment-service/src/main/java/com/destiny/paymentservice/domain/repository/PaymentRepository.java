package com.destiny.paymentservice.domain.repository;

import com.destiny.paymentservice.domain.entity.Payment;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {

    Payment save(Payment payment);

    Optional<Payment> findByOrderId(UUID orderId);

    Optional<Payment> findById(UUID paymentId);
}