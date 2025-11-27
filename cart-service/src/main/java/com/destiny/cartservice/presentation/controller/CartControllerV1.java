package com.destiny.cartservice.presentation.controller;

import com.destiny.cartservice.application.service.CartServiceV1;
import com.destiny.cartservice.presentation.dto.request.CartDeleteRequestDtoV1;
import com.destiny.cartservice.presentation.dto.request.CartSaveRequestDtoV1;
import com.destiny.cartservice.presentation.dto.request.CartUpdateQuantityRequestDtoV1;
import com.destiny.cartservice.presentation.dto.response.CartFindAllResponseDtoV1;
import com.destiny.cartservice.presentation.dto.response.CartSaveResponseDtoV1;
import com.destiny.cartservice.presentation.dto.response.CartUpdateQuantityResponseDtoV1;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/carts")
@RequiredArgsConstructor
public class CartControllerV1 {

    private final CartServiceV1 cartServiceV1;

    // 장바구니 전체 조회
    @GetMapping
    public ResponseEntity<CartFindAllResponseDtoV1> cartFindAll(Principal principal) {
        CartFindAllResponseDtoV1 response = cartServiceV1.findAllCarts(principal);
        return ResponseEntity.ok(response);

    }

    // 장바구니 담기
    @PostMapping
    public ResponseEntity<CartSaveResponseDtoV1> saveCart(Principal principal,
        @RequestBody CartSaveRequestDtoV1 request) {
        CartSaveResponseDtoV1 response = cartServiceV1.saveCartItem(principal, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 장바구니 수량 변경
    @PatchMapping("/{cartId}/quantity")
    public ResponseEntity<CartUpdateQuantityResponseDtoV1> updateCartItemQuantity(
        Principal principal, @PathVariable String cartId,
        @RequestBody CartUpdateQuantityRequestDtoV1 request) {
        CartUpdateQuantityResponseDtoV1 response = cartServiceV1.updateCartItemQuantity(principal,
            cartId, request);
        return ResponseEntity.ok(response);
    }

    // 장바구니 선택 삭제
    @DeleteMapping
    public ResponseEntity<Void> deleteCartItems(Principal principal,
        @RequestBody CartDeleteRequestDtoV1 request) {
        cartServiceV1.deleteCartItems(principal, request);
        return ResponseEntity.noContent().build();
    }
}
