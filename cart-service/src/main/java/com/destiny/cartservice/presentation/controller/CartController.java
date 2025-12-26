package com.destiny.cartservice.presentation.controller;

import com.destiny.cartservice.application.service.CartService;
import com.destiny.cartservice.infrastructure.security.auth.CustomUserDetails;
import com.destiny.cartservice.presentation.dto.request.CartDeleteRequest;
import com.destiny.cartservice.presentation.dto.request.CartSaveRequest;
import com.destiny.cartservice.presentation.dto.request.CartUpdateQuantityRequest;
import com.destiny.cartservice.presentation.dto.response.CartFindAllResponse;
import com.destiny.cartservice.presentation.dto.response.CartSaveResponse;
import com.destiny.cartservice.presentation.dto.response.CartUpdateQuantityResponse;
import com.destiny.global.code.CommonSuccessCode;
import com.destiny.global.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<ApiResponse<CartFindAllResponse>> cartFindAll(
        @AuthenticationPrincipal CustomUserDetails user) {
        CartFindAllResponse response = cartService.findAllCarts(user.getUserId());
        return ResponseEntity.ok(ApiResponse.success(CommonSuccessCode.OK, response));

    }

    // 장바구니 담기
    @PostMapping
    public ResponseEntity<ApiResponse<CartSaveResponse>> saveCart(
        @AuthenticationPrincipal CustomUserDetails user,
        @RequestBody @Valid CartSaveRequest request) {
        CartSaveResponse response = cartService.saveCartItem(user.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(CommonSuccessCode.CREATED, response));
    }

    // 장바구니 수량 변경
    @PatchMapping("/{cartId}/quantity")
    public ResponseEntity<ApiResponse<CartUpdateQuantityResponse>> updateCartItemQuantity(
        @AuthenticationPrincipal CustomUserDetails user, @PathVariable UUID cartId,
        @RequestBody @Valid CartUpdateQuantityRequest request) {
        CartUpdateQuantityResponse response = cartService.updateCartItemQuantity(user.getUserId(),
            cartId, request);
        return ResponseEntity.ok(ApiResponse.success(CommonSuccessCode.OK, response));
    }

    // 장바구니 선택 삭제
    @DeleteMapping
    public ResponseEntity<Void> deleteCartItems( @AuthenticationPrincipal CustomUserDetails user,
        @RequestBody @Valid CartDeleteRequest request) {
        cartService.deleteCartItems(user.getUserId(), request);
        return ResponseEntity.noContent().build();
    }

}