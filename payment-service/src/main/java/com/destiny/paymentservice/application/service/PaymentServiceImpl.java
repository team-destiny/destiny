package com.destiny.paymentservice.application.service;

import com.destiny.global.exception.BizException;
import com.destiny.global.response.PageResponseDto;
import com.destiny.paymentservice.application.exception.PaymentErrorCode;
import com.destiny.paymentservice.domain.entity.Payment;
import com.destiny.paymentservice.domain.repository.PaymentRepository;
import com.destiny.paymentservice.domain.vo.PaymentMethod;
import com.destiny.paymentservice.domain.vo.PaymentProvider;
import com.destiny.paymentservice.domain.vo.PaymentStatus;
import com.destiny.paymentservice.infrastructure.messaging.event.command.PaymentCommand;
import com.destiny.paymentservice.infrastructure.messaging.event.result.PaymentFailEvent;
import com.destiny.paymentservice.infrastructure.messaging.event.result.PaymentSuccessEvent;
import com.destiny.paymentservice.infrastructure.messaging.producer.PaymentProducer;
import com.destiny.paymentservice.infrastructure.security.auth.CustomUserDetails;
import com.destiny.paymentservice.presentation.dto.request.PaymentCancelRequest;
import com.destiny.paymentservice.presentation.dto.request.PaymentConfirmRequest;
import com.destiny.paymentservice.presentation.dto.response.PaymentResponse;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentProducer paymentProducer;

    // =======================================================
    // 1. 결제 요청 (PENDING 생성)
    // =======================================================
    @Override
    @Transactional
    public PaymentResponse requestPayment(PaymentCommand request) {

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
                throw new BizException(PaymentErrorCode.PAYMENT_ALREADY_APPROVED);
            }
        }

        // [2] 새로운 PENDING Payment 엔티티 생성 (기존 결제가 없거나 CANCELED/FAILED 상태인 경우)
        Payment newPayment = Payment.of(
            request.orderId(),
            request.userId(),
            request.finalAmount()
        );

        Payment savedPayment = paymentRepository.save(newPayment);

        // TODO: 고도화때 아래 코드 삭제
        PaymentConfirmRequest tmpRequest  = new PaymentConfirmRequest(request.orderId(), request.finalAmount());
        confirmPayment(tmpRequest);

        return PaymentResponse.fromEntity(savedPayment);
    }


    // =======================================================
    // 2. 결제 승인 (PENDING -> PAID)
    // =======================================================
    @Override
    @Transactional
    public PaymentResponse confirmPayment(PaymentConfirmRequest request) {
        Payment payment = null;
        try {
            // [1] 결제 내역 조회 (PENDING 상태만 승인 가능)
            payment = paymentRepository.findByOrderId(request.orderId())
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

            PaymentSuccessEvent event = PaymentSuccessEvent.builder()
                .orderId(request.orderId())
                .build();

            paymentProducer.sendSuccess(event);
        } catch (BizException e) {
            PaymentFailEvent failEvent = PaymentFailEvent.builder()
                .orderId(request.orderId())
                .errorCode(e.getResponseCode().getCode())
                .errorMessage(e.getMessage())
                .build();

            try {
                paymentProducer.sendFail(failEvent);
            } catch (Exception sendEx) {
                log.error("[handlePaymentValidate] sendFail 실패 - 수동 개입 필요: orderId={}", request.orderId(), sendEx);
            }
        }
        return PaymentResponse.fromEntity(payment);
    }

    // =======================================================
    // 3. 결제 취소 (PAID -> CANCELED)
    // =======================================================
    @Override
    @Transactional
    public PaymentResponse cancelPayment(PaymentCancelRequest request, CustomUserDetails userDetails) {
        // [1] 결제 내역 조회 (PAID 상태여야 취소 가능)
        Payment payment = paymentRepository.findByOrderId(request.orderId())
            .orElseThrow(() -> new BizException(PaymentErrorCode.PAYMENT_NOT_FOUND));

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

    // =======================================================
    // 4. 결제 단건 조회
    // =======================================================
    @Override
    public PaymentResponse getPaymentByOrderId(UUID orderId, CustomUserDetails userDetails) {
        Payment payment = switch (userDetails.getUserRole()) {
            case "CUSTOMER" -> paymentRepository.findByOrderIdAndUserId(orderId, userDetails.getUserId()).orElseThrow(() -> new BizException(PaymentErrorCode.PAYMENT_NOT_FOUND));
            case "MASTER" -> paymentRepository.findByOrderId(orderId).orElseThrow(() -> new BizException(PaymentErrorCode.PAYMENT_NOT_FOUND));
            default -> throw new BizException(PaymentErrorCode.FORBIDDEN_ACCESS);
        };

        return PaymentResponse.fromEntity(payment);
    }

    // =======================================================
    // 5. 결제 내역 전체 조회 (페이징)
    // =======================================================
    @Override
    @Transactional(readOnly = true)
    public PageResponseDto<PaymentResponse> getAllPayments(Pageable pageable, CustomUserDetails userDetails) {

        Page<Payment> paymentPage = switch (userDetails.getUserRole()) {
            case "CUSTOMER" -> paymentRepository.findAllByUserId(userDetails.getUserId(), pageable);
            case "MASTER" -> paymentRepository.findAll(pageable);
            default -> throw new BizException(PaymentErrorCode.FORBIDDEN_ACCESS);
        };

        Page<PaymentResponse> responsePage = paymentPage.map(PaymentResponse::fromEntity);

        return PageResponseDto.from(responsePage);
    }
}