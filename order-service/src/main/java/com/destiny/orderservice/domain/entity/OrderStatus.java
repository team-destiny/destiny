package com.destiny.orderservice.domain.entity;

public enum OrderStatus {

    CREATED,
    WAITING_PAYMENT,
    FAILED,
    CANCELED,
    COMPLETED
}
