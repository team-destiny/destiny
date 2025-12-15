package com.destiny.paymentservice.application.service.inter;

import com.destiny.paymentservice.domain.vo.PaymentProvider;

public interface PgSelectionPolicy {

    PaymentProvider select();
}