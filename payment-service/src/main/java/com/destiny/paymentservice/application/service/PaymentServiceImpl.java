package com.destiny.paymentservice.application.service;

import com.destiny.global.exception.BizException;
import com.destiny.paymentservice.application.exception.PaymentErrorCode;
import com.destiny.paymentservice.domain.entity.Payment;
import com.destiny.paymentservice.domain.repository.PaymentRepository;
import com.destiny.paymentservice.domain.vo.PaymentStatus; // PaymentStatus 임포트 추가
import com.destiny.paymentservice.presentation.dto.request.PaymentCancelRequest;
import com.destiny.paymentservice.presentation.dto.request.PaymentConfirmRequest;
import com.destiny.paymentservice.presentation.dto.request.PaymentRequest;
import com.destiny.paymentservice.presentation.dto.response.PaymentResponse;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    // =======================================================
    // 1. 결제 요청 (PENDING 생성)
    // =======================================================
    @Override
    @Transactional
    public PaymentResponse requestPayment(PaymentRequest request) {

        //️ [1] 주문 ID 중복 확인 및 상태에 따른 분기 처리 (Optional 사용)
        Optional<Payment> findPayment = paymentRepository.findByOrderId(request.orderId());

        if (findPayment.isPresent()) {
            if (findPayment.get().getPaymentStatus().equals(PaymentStatus.PENDING)) {
                // PENDING 상태: 기존 정보를 그대로 반환 (confirm 시도를 유도)
                return PaymentResponse.fromEntity(findPayment.get());
            }
            if (findPayment.get().getPaymentStatus().equals(PaymentStatus.PAID)) {
                // PAID 상태: 이미 최종 결제 완료 (중복 결제 시도 방지)
                throw new BizException(PaymentErrorCode.PAYMENT_ALREADY_APPROVED);
            }
        }

        // [2] 새로운 PENDING Payment 엔티티 생성 (기존 결제가 없거나 CANCELED/FAILED 상태인 경우)
        Payment newPayment = Payment.of(
            request.orderId(),
            request.pgTxId(),
            request.paymentType(),
            request.amount()
        );

        Payment savedPayment = paymentRepository.save(newPayment);

        return PaymentResponse.fromEntity(savedPayment);
    }


    // =======================================================
    // 2. 결제 승인 (PENDING -> PAID)
    // =======================================================
    @Override
    @Transactional
    public PaymentResponse confirmPayment(PaymentConfirmRequest request) {
        // [1] 결제 내역 조회 (PENDING 상태만 승인 가능)
        Payment payment = paymentRepository.findByOrderId(request.orderId())
            .orElseThrow(() -> new BizException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        // [2] PENDING 상태인지 확인
        if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new BizException(PaymentErrorCode.PAYMENT_NOT_PENDING);
        }

        // [3] 실제 PG 연동 로직

        // [4] 도메인 행위 호출 (상태 변경: PENDING -> PAID)
        payment.paid(request.pgTxId(), request.paymentType());

        return PaymentResponse.fromEntity(payment);
    }

    // =======================================================
    // 3. 결제 취소 (PAID -> CANCELED)
    // =======================================================
    @Override
    @Transactional
    public PaymentResponse cancelPayment(PaymentCancelRequest request) {
        // [1] 결제 내역 조회 (PAID 상태여야 취소 가능)
        Payment payment = paymentRepository.findByOrderId(request.orderId())
            .orElseThrow(() -> new BizException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        // [2] PAID 상태인지 확인
        if (payment.getPaymentStatus() != PaymentStatus.PAID) {
            throw new BizException(PaymentErrorCode.PAYMENT_NOT_PAID);
        }

        // [3] 실제 PG 연동 로직

        // [4] 도메인 행위 호출 (상태 변경: PAID -> CANCELED)
        payment.cancel();

        return PaymentResponse.fromEntity(payment);
    }

    // =======================================================
    // 4. 결제 조회
    // =======================================================
    @Override
    public PaymentResponse getPaymentByOrderId(UUID orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
            .orElseThrow(() -> new BizException(PaymentErrorCode.PAYMENT_NOT_FOUND));
        return PaymentResponse.fromEntity(payment);
    }
}