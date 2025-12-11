package com.destiny.cartservice.application.service;

import com.destiny.cartservice.application.dto.event.CartClearEvent;
import com.destiny.cartservice.presentation.dto.request.CartDeleteRequest;
import com.destiny.cartservice.presentation.dto.request.CartSaveRequest;
import com.destiny.cartservice.presentation.dto.request.CartUpdateQuantityRequest;
import com.destiny.cartservice.presentation.dto.response.CartFindAllResponse;
import com.destiny.cartservice.presentation.dto.response.CartSaveResponse;
import com.destiny.cartservice.presentation.dto.response.CartUpdateQuantityResponse;
import java.security.Principal;
import java.util.UUID;

public interface CartService {

    CartFindAllResponse findAllCarts(UUID userId);

    CartSaveResponse saveCartItem(UUID userId, CartSaveRequest request);

    CartUpdateQuantityResponse updateCartItemQuantity(
        UUID userId,
        UUID cartId,
        CartUpdateQuantityRequest request
    );

    void deleteCartItems(UUID userId, CartDeleteRequest request);

    void clearCart(CartClearEvent event);
}