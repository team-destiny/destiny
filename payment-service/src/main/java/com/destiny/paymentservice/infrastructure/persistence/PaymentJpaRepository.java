package com.destiny.paymentservice.infrastructure.persistence;

import com.destiny.paymentservice.domain.entity.Payment;
import com.destiny.paymentservice.domain.port.PaymentRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentJpaRepository extends JpaRepository<Payment, UUID>, PaymentRepository {

    @Override
    Optional<Payment> findByOrderId(String orderId);

    @Override
    Optional<Payment> findByPgTxId(String pgTxId);

}