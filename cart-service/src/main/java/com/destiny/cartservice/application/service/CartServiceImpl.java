package com.destiny.cartservice.application.service;

import com.destiny.cartservice.presentation.dto.request.CartDeleteRequest;
import com.destiny.cartservice.presentation.dto.request.CartSaveRequest;
import com.destiny.cartservice.presentation.dto.request.CartUpdateQuantityRequest;
import com.destiny.cartservice.presentation.dto.response.CartFindAllResponse;
import com.destiny.cartservice.presentation.dto.response.CartSaveResponse;
import com.destiny.cartservice.presentation.dto.response.CartUpdateQuantityResponse;
import java.security.Principal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    @Override
    @Transactional(readOnly = true)
    public CartFindAllResponse findAllCarts(Principal principal) {
        return null;
    }

    @Override
    public CartSaveResponse saveCartItem(Principal principal, CartSaveRequest request) {
        return null;
    }

    @Override
    public CartUpdateQuantityResponse updateCartItemQuantity(
        Principal principal,
        UUID cartId,
        CartUpdateQuantityRequest request
    ) {
        return null;
    }


    @Override
    public void deleteCartItems(Principal principal, CartDeleteRequest request) {
    }
}
