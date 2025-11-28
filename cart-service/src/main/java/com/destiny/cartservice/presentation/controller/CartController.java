package com.destiny.cartservice.presentation.controller;

import com.destiny.cartservice.application.service.CartService;
import com.destiny.cartservice.presentation.dto.request.CartDeleteRequest;
import com.destiny.cartservice.presentation.dto.request.CartSaveRequest;
import com.destiny.cartservice.presentation.dto.request.CartUpdateQuantityRequest;
import com.destiny.cartservice.presentation.dto.response.CartFindAllResponse;
import com.destiny.cartservice.presentation.dto.response.CartSaveResponse;
import com.destiny.cartservice.presentation.dto.response.CartUpdateQuantityResponse;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.UUID;
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
public class CartController {

    private final CartService cartService;

    // 장바구니 전체 조회
    @GetMapping
    public ResponseEntity<CartFindAllResponse> cartFindAll(Principal principal) {
        CartFindAllResponse response = cartService.findAllCarts(principal);
        return ResponseEntity.ok(response);

    }

    // 장바구니 담기
    @PostMapping
    public ResponseEntity<CartSaveResponse> saveCart(Principal principal,
        @RequestBody @Valid CartSaveRequest request) {
        CartSaveResponse response = cartService.saveCartItem(principal, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 장바구니 수량 변경
    @PatchMapping("/{cartId}/quantity")
    public ResponseEntity<CartUpdateQuantityResponse> updateCartItemQuantity(
        Principal principal, @PathVariable UUID cartId,
        @RequestBody @Valid CartUpdateQuantityRequest request) {
        CartUpdateQuantityResponse response = cartService.updateCartItemQuantity(principal,
            cartId, request);
        return ResponseEntity.ok(response);
    }

    // 장바구니 선택 삭제
    @DeleteMapping
    public ResponseEntity<Void> deleteCartItems(Principal principal,
        @RequestBody CartDeleteRequest request) {
        cartService.deleteCartItems(principal, request);
        return ResponseEntity.noContent().build();
    }
}