package com.destiny.paymentservice.domain.vo;

public enum PaymentMethod {
    CARD,           // 카드 결제
    VIRTUAL_ACCOUNT, // 가상계좌
    BANK_TRANSFER,   // 계좌이체
    EASY_PAY,        // 네이버/카카오 등 간편결제
    MOBILE,          // 휴대폰 결제
    ETC;              // 기타

    public static PaymentMethod random() {
        PaymentMethod[] methods = values();
        return methods[(int)(Math.random() * methods.length)];
    }
}