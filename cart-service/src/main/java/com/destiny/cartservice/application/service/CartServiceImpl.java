package com.destiny.cartservice.application.service;

import com.destiny.cartservice.application.dto.event.CartClearEvent;
import com.destiny.cartservice.application.service.exception.CartErrorCode;
import com.destiny.cartservice.domain.model.Cart;
import com.destiny.cartservice.domain.repository.CartRepository;
import com.destiny.cartservice.infrastructure.client.ProductClient;
import com.destiny.cartservice.infrastructure.client.dto.ProductResponse;
import com.destiny.cartservice.presentation.dto.request.CartDeleteRequest;
import com.destiny.cartservice.presentation.dto.request.CartSaveRequest;
import com.destiny.cartservice.presentation.dto.request.CartUpdateQuantityRequest;
import com.destiny.cartservice.presentation.dto.response.CartFindAllResponse;
import com.destiny.cartservice.presentation.dto.response.CartFindItemResponse;
import com.destiny.cartservice.presentation.dto.response.CartSaveResponse;
import com.destiny.cartservice.presentation.dto.response.CartUpdateQuantityResponse;
import com.destiny.global.exception.BizException;
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
    private final ProductClient productClient;

    /*
    장바구니 전체 조회
    */
    @Override
    @Transactional(readOnly = true)
    public CartFindAllResponse findAllCarts(UUID userId) {

        List<Cart> carts = cartRepository.findAllByUserId(userId);

        List<CartFindItemResponse> items = carts.stream()
            .map(this::toItemResponse)
            .toList();

        return CartFindAllResponse.from(items);
    }

    private CartFindItemResponse toItemResponse(Cart cart) {

        ProductResponse product = productClient.getProductById(cart.getProductId());

        if (product.name() == null) {
            throw new BizException(CartErrorCode.NOT_FOUND_PRODUCT_DATA);
        }

        String optionInfo = product.color() + " / " + product.size();

        return CartFindItemResponse.builder()
            .cartId(cart.getId())
            .productId(cart.getProductId())
            .brandId(product.brandId())
            .quantity(cart.getQuantity())
            .productName(product.name())
            .optionName(optionInfo)
            .price(product.price())
            .build();
    }

    /* 장바구니 담기
     * 같은 상품과 오션이 존재 --> 수량만 증가
     * 없으면 새 장바구니 생성
     * */
    @Override
    public CartSaveResponse saveCartItem(UUID userId, CartSaveRequest request) {

        Cart cart = cartRepository.findExistingCart(userId, request.getProductId())
            .map(existing -> {
                existing.updateQuantity(existing.getQuantity() + request.getQuantity());
                return existing;
            })
            .orElseGet(() -> Cart.of(
                userId,
                request.getProductId(),
                request.getQuantity()
            ));

        Cart savedCart = cartRepository.save(cart);

        return toSaveResponse(savedCart);
    }

    private CartSaveResponse toSaveResponse(Cart cart) {

        ProductResponse product = productClient.getProductById(cart.getProductId());

        if (product.name() == null) {
            throw new BizException(CartErrorCode.NOT_FOUND_PRODUCT_DATA);
        }

        String optionInfo = product.color() + " / " + product.size();

        return CartSaveResponse.builder()
            .cartId(cart.getId())
            .productId(cart.getProductId())
            .brandId(product.brandId())
            .quantity(cart.getQuantity())
            .productName(product.name())
            .optionName(optionInfo)
            .price(product.price())
            .build();
    }

    /* 장바구니 수량 변경 */
    @Override
    public CartUpdateQuantityResponse updateCartItemQuantity(
        UUID userId,
        UUID cartId,
        CartUpdateQuantityRequest request
    ) {
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

        ProductResponse product = productClient.getProductById(cart.getProductId());

        if (product.name() == null) {
            throw new BizException(CartErrorCode.NOT_FOUND_PRODUCT_DATA);
        }

        String optionInfo = product.color() + " / " + product.size();

        return CartUpdateQuantityResponse.builder()
            .cartId(cart.getId())
            .productId(cart.getProductId())
            .brandId(product.brandId())
            .quantity(cart.getQuantity())
            .productName(product.name())
            .optionName(optionInfo)
            .price(product.price())
            .build();
    }

    /* 장바구니 선택 삭제 */
    @Override
    public void deleteCartItems(UUID userId, CartDeleteRequest request) {
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

    @Override
    public void clearCart(CartClearEvent event) {
        if (event == null || event.cartId() == null) {
            throw new BizException(CartErrorCode.INVALID_CLEAR_EVENT);
        }

        cartRepository.deleteAllByIdIn(List.of(event.cartId()));
    }
}