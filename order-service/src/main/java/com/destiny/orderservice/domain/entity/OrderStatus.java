package com.destiny.orderservice.domain.entity;

public enum OrderStatus {

    PENDING,
    CREATED,
    WAITING_PAYMENT,
    FAILED,
    CANCELED,
    COMPLETED
}
