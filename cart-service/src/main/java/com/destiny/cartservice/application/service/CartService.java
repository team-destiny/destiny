package com.destiny.cartservice.application.service;

import com.destiny.cartservice.presentation.dto.request.CartDeleteRequest;
import com.destiny.cartservice.presentation.dto.request.CartSaveRequest;
import com.destiny.cartservice.presentation.dto.request.CartUpdateQuantityRequest;
import com.destiny.cartservice.presentation.dto.response.CartFindAllResponse;
import com.destiny.cartservice.presentation.dto.response.CartSaveResponse;
import com.destiny.cartservice.presentation.dto.response.CartUpdateQuantityResponse;
import java.security.Principal;
import java.util.UUID;

public interface CartService {

    CartFindAllResponse findAllCarts(Principal principal);

    CartSaveResponse saveCartItem(Principal principal, CartSaveRequest request);

    CartUpdateQuantityResponse updateCartItemQuantity(
        Principal principal,
        UUID cartId,
        CartUpdateQuantityRequest request
    );

    void deleteCartItems(Principal principal, CartDeleteRequest request);
}