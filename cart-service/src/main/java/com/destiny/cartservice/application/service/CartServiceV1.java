package com.destiny.cartservice.application.service;

import com.destiny.cartservice.presentation.dto.request.CartDeleteRequestDtoV1;
import com.destiny.cartservice.presentation.dto.request.CartSaveRequestDtoV1;
import com.destiny.cartservice.presentation.dto.request.CartUpdateQuantityRequestDtoV1;
import com.destiny.cartservice.presentation.dto.response.CartFindAllResponseDtoV1;
import com.destiny.cartservice.presentation.dto.response.CartSaveResponseDtoV1;
import com.destiny.cartservice.presentation.dto.response.CartUpdateQuantityResponseDtoV1;
import java.security.Principal;
import org.springframework.stereotype.Service;

public interface CartServiceV1 {

    CartFindAllResponseDtoV1 findAllCarts(Principal principal);

    CartSaveResponseDtoV1 saveCartItem(Principal principal, CartSaveRequestDtoV1 request);

    CartUpdateQuantityResponseDtoV1 updateCartItemQuantity(
        Principal principal,
        String cartId,
        CartUpdateQuantityRequestDtoV1 request
    );

    void deleteCartItems(Principal principal, CartDeleteRequestDtoV1 request);
}