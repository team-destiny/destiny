package com.destiny.paymentservice.application.service.router;

import com.destiny.paymentservice.application.service.inter.PgSelectionPolicy;
import com.destiny.paymentservice.domain.vo.PaymentProvider;
import org.springframework.stereotype.Component;

@Component
public class PgSelectionPolicyImpl implements PgSelectionPolicy {

    @Override
    public PaymentProvider select() {
        // TODO: 레디스 조회 후 mainPG반환
        return PaymentProvider.MOCK;
    }
}