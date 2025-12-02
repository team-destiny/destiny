package com.destiny.paymentservice.domain.port;

import com.destiny.paymentservice.domain.entity.Payment;
import java.util.Optional;

public interface PaymentRepository {

    /**
     * 새로운 결제 정보를 저장하거나 기존 결제 정보를 갱신합니다.
     * @param payment 저장할 Payment 엔티티
     * @return 저장된 Payment 엔티티
     */
    Payment save(Payment payment);

    /**
     * 운명팀의 고유 주문 번호(orderId)를 기준으로 결제 정보를 조회합니다.
     * @param orderId 주문 번호
     * @return Optional<Payment> 결제 정보
     */
    Optional<Payment> findByOrderId(String orderId);

    /**
     * PG사 거래 고유 번호(pgTxId)를 기준으로 결제 정보를 조회합니다.
     * @param pgTxId PG사 거래 고유 ID (Toss의 paymentKey 등)
     * @return Optional<Payment> 결제 정보
     */
    Optional<Payment> findByPgTxId(String pgTxId);

}