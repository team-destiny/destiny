package com.destiny.cartservice.application.service;

import com.destiny.cartservice.presentation.dto.request.CartDeleteRequestDtoV1;
import com.destiny.cartservice.presentation.dto.request.CartSaveRequestDtoV1;
import com.destiny.cartservice.presentation.dto.request.CartUpdateQuantityRequestDtoV1;
import com.destiny.cartservice.presentation.dto.response.CartFindAllResponseDtoV1;
import com.destiny.cartservice.presentation.dto.response.CartSaveResponseDtoV1;
import com.destiny.cartservice.presentation.dto.response.CartUpdateQuantityResponseDtoV1;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartServiceV1 {

    @Override
    @Transactional(readOnly = true)
    public CartFindAllResponseDtoV1 findAllCarts(Principal principal) {
        return null;
    }

    @Override
    public CartSaveResponseDtoV1 saveCartItem(Principal principal, CartSaveRequestDtoV1 request) {
        return null;
    }

    @Override
    public CartUpdateQuantityResponseDtoV1 updateCartItemQuantity(
        Principal principal,
        String cartId,
        CartUpdateQuantityRequestDtoV1 request
    ) {
        return null;
    }


    @Override
    public void deleteCartItems(Principal principal, CartDeleteRequestDtoV1 request) {
    }
}
