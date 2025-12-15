package com.destiny.paymentservice.application.service.impl;

import com.destiny.global.exception.BizException;
import com.destiny.paymentservice.application.exception.PaymentErrorCode;
import com.destiny.paymentservice.application.service.inter.PaymentService;
import com.destiny.paymentservice.domain.entity.Payment;
import com.destiny.paymentservice.domain.repository.PaymentRepository;
import com.destiny.paymentservice.domain.vo.PaymentMethod;
import com.destiny.paymentservice.domain.vo.PaymentProvider;
import com.destiny.paymentservice.domain.vo.PaymentStatus;
import com.destiny.paymentservice.infrastructure.messaging.event.command.PaymentCommand;
import com.destiny.paymentservice.infrastructure.messaging.event.result.PaymentFailEvent;
import com.destiny.paymentservice.infrastructure.messaging.event.result.PaymentSuccessEvent;
import com.destiny.paymentservice.infrastructure.messaging.producer.PaymentConfirmProducer;
import com.destiny.paymentservice.infrastructure.messaging.producer.PaymentCreateProducer;
import com.destiny.paymentservice.infrastructure.security.auth.CustomUserDetails;
import com.destiny.paymentservice.presentation.dto.request.PaymentCancelRequest;
import com.destiny.paymentservice.presentation.dto.request.PaymentConfirmRequest;
import com.destiny.paymentservice.presentation.dto.response.PaymentResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentCreateProducer paymentCreateProducer;
    private final PaymentConfirmProducer paymentConfirmProducer;

    @Override
    public PaymentProvider supports() {
        return PaymentProvider.MOCK;
    }

    // 결제 요청 (PENDING 생성)
    @Transactional
    public PaymentResponse requestPayment(PaymentCommand request) {

        try {
            //️ [1] 주문 ID 중복 확인 및 상태에 따른 분기 처리 (Optional 사용)
            Optional<Payment> findPayment = paymentRepository.findByOrderId(request.orderId());

            if (findPayment.isPresent()) {
                if (!findPayment.get().getUserId().equals(request.userId())) {
                    throw new BizException(PaymentErrorCode.FORBIDDEN_ACCESS);
                }
                if (findPayment.get().getPaymentStatus().equals(PaymentStatus.PENDING)) {
                    // PENDING 상태: 기존 정보를 그대로 반환 (confirm 시도를 유도)
                    return PaymentResponse.fromEntity(findPayment.get());
                }
                if (findPayment.get().getPaymentStatus().equals(PaymentStatus.PAID)) {
                    // PAID 상태: 이미 최종 결제 완료 (중복 결제 시도 방지)
                    return PaymentResponse.fromEntity(findPayment.get());
                }
            }

            // [2] 새로운 PENDING Payment 엔티티 생성 (기존 결제가 없거나 CANCELED/FAILED 상태인 경우)
            Payment newPayment = Payment.of(request.orderId(), request.userId(), request.finalAmount());
            Payment savedPayment = paymentRepository.save(newPayment);

            PaymentSuccessEvent event = PaymentSuccessEvent.builder().orderId(request.orderId()).build();
            paymentCreateProducer.sendCreateSuccess(event);

            return PaymentResponse.fromEntity(savedPayment);

        } catch (BizException e) {
            PaymentFailEvent failEvent = PaymentFailEvent.builder()
                .orderId(request.orderId())
                .errorCode(e.getResponseCode().getCode())
                .errorMessage(e.getMessage())
                .build();

            try {
                paymentCreateProducer.sendCreateFail(failEvent);
            } catch (Exception sendEx) {
                log.error("[handlePaymentValidate] sendFail 실패 - 수동 개입 필요: orderId={}", request.orderId(), sendEx);
            }

            throw e;
        }
    }

    // 결제 승인 (PENDING -> PAID)
    @Override
    @Transactional
    public PaymentResponse confirmPayment(PaymentConfirmRequest request) {
        try {
            // [1] 결제 내역 조회 (PENDING 상태만 승인 가능)
            Payment payment = paymentRepository.findByOrderId(request.orderId())
                .orElseThrow(() -> new BizException(PaymentErrorCode.PAYMENT_NOT_FOUND));

            // TODO: 파라미터로 userDetails를 받을지 결정
//            if (!payment.getUserId().equals(userDetails.getUserId())) {
//                throw new BizException(PaymentErrorCode.PAYMENT_OWNER_MISMATCH);
//            }

            // [2] PENDING 상태인지 확인
            if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
                throw new BizException(PaymentErrorCode.PAYMENT_NOT_PENDING);
            }

            // [3] 실제 PG 연동 로직

            // [4] 도메인 행위 호출 (상태 변경: PENDING -> PAID)
            // 실제연동시 결제사 api 요청 후 받아온 값으로 저장
            payment.paid(PaymentProvider.MOCK, PaymentMethod.random());
            PaymentSuccessEvent event = PaymentSuccessEvent.builder().orderId(request.orderId()).build();

            paymentConfirmProducer.sendSuccess(event);
            return PaymentResponse.fromEntity(payment);
        } catch (BizException e) {
            PaymentFailEvent failEvent = PaymentFailEvent.builder()
                .orderId(request.orderId())
                .errorCode(e.getResponseCode().getCode())
                .errorMessage(e.getMessage())
                .build();

            try {
                paymentConfirmProducer.sendFail(failEvent);
            } catch (Exception sendEx) {
                log.error("[handlePaymentConfirm] sendFail 실패 - 수동 개입 필요: orderId={}", request.orderId(), sendEx);
            }
            throw e;
        }
    }

    // 결제 취소 (PAID -> CANCELED)
    @Override
    @Transactional
    public PaymentResponse cancelPayment(PaymentCancelRequest request, CustomUserDetails userDetails) {
        // [1] 결제 내역 조회 (PAID 상태여야 취소 가능)
        Payment payment = paymentRepository.findByOrderId(request.orderId())
            .orElseThrow(() -> new BizException(PaymentErrorCode.PAYMENT_NOT_FOUND));

//        log.info("payment.userId={}, login.userId={}", payment.getUserId(), userDetails.getUserId());

        if (!payment.getUserId().equals(userDetails.getUserId())) {
            throw new BizException(PaymentErrorCode.PAYMENT_OWNER_MISMATCH);
        }

        // [2] PAID 상태인지 확인
        if (payment.getPaymentStatus() != PaymentStatus.PAID) {
            throw new BizException(PaymentErrorCode.PAYMENT_NOT_PAID);
        }

        // [3] 실제 PG 연동 로직

        // [4] 도메인 행위 호출 (상태 변경: PAID -> CANCELED)
        payment.cancel();

        return PaymentResponse.fromEntity(payment);
    }
}