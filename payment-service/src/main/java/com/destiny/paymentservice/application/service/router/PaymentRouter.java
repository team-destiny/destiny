package com.destiny.paymentservice.application.service.router;

import com.destiny.global.exception.BizException;
import com.destiny.paymentservice.application.exception.PaymentErrorCode;
import com.destiny.paymentservice.application.service.inter.PaymentService;
import com.destiny.paymentservice.application.service.inter.PgSelectionPolicy;
import com.destiny.paymentservice.domain.vo.PaymentProvider;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class PaymentRouter {

    private final PgSelectionPolicy pgSelectionPolicy;
    private final Map<PaymentProvider, PaymentService> serviceMap;

    public PaymentRouter(PgSelectionPolicy pgSelectionPolicy, List<PaymentService> services) {
        this.pgSelectionPolicy = pgSelectionPolicy;
        this.serviceMap = services.stream()
            .collect(Collectors.toMap(
                PaymentService::supports,
                Function.identity()
            ));
    }

    public PaymentService route() {
        PaymentProvider provider = pgSelectionPolicy.select();
        PaymentService service = serviceMap.get(provider);
        if (service == null) {
            throw new BizException(PaymentErrorCode.UNSUPPORTED_PAYMENT_PROVIDER);
        }
        return service;
    }
}
