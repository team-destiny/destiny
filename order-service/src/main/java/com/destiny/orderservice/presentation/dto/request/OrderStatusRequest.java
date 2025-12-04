package com.destiny.orderservice.presentation.dto.request;

import com.destiny.orderservice.domain.entity.OrderStatus;

public record OrderStatusRequest(
    OrderStatus orderStatus
) {

}
