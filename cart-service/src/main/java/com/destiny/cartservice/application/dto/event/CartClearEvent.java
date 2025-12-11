package com.destiny.cartservice.application.dto.event;

import java.util.UUID;

public record CartClearEvent (
    UUID cartId
) {}
