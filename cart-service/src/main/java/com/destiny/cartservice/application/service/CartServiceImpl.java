package com.destiny.cartservice.application.service;

import com.destiny.cartservice.application.service.exception.CartErrorCode;
import com.destiny.cartservice.domain.model.Cart;
import com.destiny.cartservice.domain.repository.CartRepository;
import com.destiny.cartservice.presentation.dto.request.CartDeleteRequest;
import com.destiny.cartservice.presentation.dto.request.CartSaveRequest;
import com.destiny.cartservice.presentation.dto.request.CartUpdateQuantityRequest;
import com.destiny.cartservice.presentation.dto.response.CartFindAllResponse;
import com.destiny.cartservice.presentation.dto.response.CartFindItemResponse;
import com.destiny.cartservice.presentation.dto.response.CartSaveResponse;
import com.destiny.cartservice.presentation.dto.response.CartUpdateQuantityResponse;
import com.destiny.global.exception.BizException;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    /*
    장바구니 전체 조회
    */
    @Override
    @Transactional(readOnly = true)
    public CartFindAllResponse findAllCarts(Principal principal) {
        UUID userId = getUserId(principal);

        List<Cart> carts = cartRepository.findAllByUserId(userId);

        List<CartFindItemResponse> items = carts.stream()
            .map(this::toItemResponse)
            .toList();

        return CartFindAllResponse.from(items);
    }

    private UUID getUserId(Principal principal) {
        return UUID.fromString(principal.getName());
    }


    private CartFindItemResponse toItemResponse(Cart cart) {
        // TODO: 상품/옵션/가격 조회 붙이면 적용
        return CartFindItemResponse.builder()
            .cartId(cart.getId())
            .productId(cart.getProductId())
            .optionId(cart.getOptionId())
            .quantity(cart.getQuantity())
            .productName(null)
            .optionName(null)
            .price(0)
            .build();
    }

    /* 장바구니 담기
     * 같은 상품과 오션이 존재 --> 수량만 증가
     * 없으면 새 장바구니 생성
     * */
    @Override
    public CartSaveResponse saveCartItem(Principal principal, CartSaveRequest request) {
        UUID userId = getUserId(principal);

        Cart cart = cartRepository.findExistingCart(userId, request.getProductId(),
                request.getOptionId())
            .map(existing -> {
                existing.updateQuantity(existing.getQuantity() + request.getQuantity());
                return existing;
            })
            .orElseGet(() -> Cart.of(
                userId,
                request.getProductId(),
                request.getOptionId(),
                request.getQuantity()
            ));

        Cart savedCart = cartRepository.save(cart);

        return toSaveResponse(savedCart);
    }

    private CartSaveResponse toSaveResponse(Cart cart) {
        // TODO: 상품 정보 붙이면 추가
        return CartSaveResponse.builder()
            .cartId(cart.getId())
            .productId(cart.getProductId())
            .optionId(cart.getOptionId())
            .quantity(cart.getQuantity())
            .productName(null)
            .optionName(null)
            .price(0)
            .build();
    }

    /* 장바구니 수량 변경 */
    @Override
    public CartUpdateQuantityResponse updateCartItemQuantity(
        Principal principal,
        UUID cartId,
        CartUpdateQuantityRequest request
    ) {
        UUID userId = getUserId(principal);

        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new BizException(CartErrorCode.CART_NOT_FOUND));

        if (!cart.getUserId().equals(userId)) {
            throw new BizException(CartErrorCode.INVALID_OWNER);
        }

        cart.updateQuantity(request.getQuantity());
        Cart updated = cartRepository.save(cart);

        return toUpdateQuantityResponse(updated);

    }

    private CartUpdateQuantityResponse toUpdateQuantityResponse(Cart cart) {
        // TODO: 상품 정보 붙이면 추가
        return CartUpdateQuantityResponse.builder()
            .cartId(cart.getId())
            .productId(cart.getProductId())
            .optionId(cart.getOptionId())
            .quantity(cart.getQuantity())
            .productName(null)
            .optionName(null)
            .price(0)
            .build();
    }

    /* 장바구니 선택 삭제 */
    @Override
    public void deleteCartItems(Principal principal, CartDeleteRequest request) {
        UUID userId = getUserId(principal);
        List<UUID> cartIds = request.getCartIds();

        List<Cart> ownedCarts = cartRepository.findAllByUserId(userId);
        List<UUID> ownedIds = ownedCarts.stream()
            .map(Cart::getId)
            .toList();

        boolean allOwned = cartIds.stream().allMatch(ownedIds::contains);

        if (!allOwned) {
            throw new BizException(CartErrorCode.INVALID_DELETE_REQUEST);
        }

        cartRepository.deleteAllByIdIn(cartIds);
    }
}
