package com.destiny.stockservice.application;

public interface StockEventPublisher {

    void publishReservationSuccess(String event);

    void publishReservationFail(String event);

    void publishReservationCancelSuccess(String event);

    void publishReservationCancelFail(String event);

    void publishConfirmedStockCancelSuccess(String event);

    void publishConfirmedStockCancelFail(String event);

    void publishProductSoldOut(String event);

    void publishProductReopen(String event);
}
