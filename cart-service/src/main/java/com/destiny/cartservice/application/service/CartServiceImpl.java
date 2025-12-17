package com.destiny.cartservice.application.service;

import com.destiny.cartservice.application.dto.event.CartClearEvent;
import com.destiny.cartservice.application.service.exception.CartErrorCode;
import com.destiny.cartservice.domain.model.Cart;
import com.destiny.cartservice.domain.repository.CartRepository;
import com.destiny.cartservice.infrastructure.client.ProductClient;
import com.destiny.cartservice.infrastructure.client.dto.ProductResponse;
import com.destiny.cartservice.infrastructure.client.dto.ProductStatus;
import com.destiny.cartservice.presentation.dto.request.CartDeleteRequest;
import com.destiny.cartservice.presentation.dto.request.CartSaveRequest;
import com.destiny.cartservice.presentation.dto.request.CartUpdateQuantityRequest;
import com.destiny.cartservice.presentation.dto.response.CartFindAllResponse;
import com.destiny.cartservice.presentation.dto.response.CartFindItemResponse;
import com.destiny.cartservice.presentation.dto.response.CartSaveResponse;
import com.destiny.cartservice.presentation.dto.response.CartUpdateQuantityResponse;
import com.destiny.global.exception.BizException;
import feign.FeignException;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductClient productClient;
    private final RedisTemplate<String, Object> redisTemplate;


    private static final String CART_CACHE_PREFIX = "cart:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(30);

    /*
    장바구니 전체 조회
    */
    @Override
    @Transactional(readOnly = true)
    public CartFindAllResponse findAllCarts(UUID userId) {

        String cacheKey = CART_CACHE_PREFIX + userId;

        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached instanceof CartFindAllResponse cachedResponse) {
            log.info("[Cache HIT] 캐시에서 장바구니 조회. userId: {}", userId);
            return cachedResponse;
        }

        log.info("[Cache MISS] DB에서 장바구니 조회. userId: {}", userId);

        List<Cart> carts = cartRepository.findAllByUserId(userId);

        List<CartFindItemResponse> items = carts.stream()
            .map(this::toItemResponse)
            .toList();

        CartFindAllResponse response = CartFindAllResponse.from(items);

        redisTemplate.opsForValue().set(cacheKey, response, CACHE_TTL);

        log.info("[Cache SAVE] 캐시에 장바구니 저장. userId: {}, TTL: 30분", userId);

        return response;
    }

    private ProductResponse fetchProduct(UUID productId) {
        ProductResponse product;
        try {
            product = productClient.getProductById(productId);
        } catch (FeignException.NotFound e) {
            throw new BizException(CartErrorCode.NOT_FOUND_PRODUCT_DATA);
        } catch (FeignException e) {
            throw new BizException(CartErrorCode.PRODUCT_SERVICE_UNAVAILABLE);
        }

        if (product == null || product.name() == null ||
            product.color() == null || product.size() == null ||
            product.price() == null || product.brandId() == null ||
            product.status() == null) {
            throw new BizException(CartErrorCode.NOT_FOUND_PRODUCT_DATA);
        }

        return product;
    }

    private ProductResponse fetchProductForAction(UUID productId) {
        ProductResponse product = fetchProduct(productId);

        if (product.status() == ProductStatus.HIDED || product.status() == ProductStatus.OUT_OF_STOCK) {
            throw new BizException(CartErrorCode.CANNOT_ADD_UNAVAILABLE_PRODUCT);
        }

        return product;
    }

    private CartFindItemResponse toItemResponse(Cart cart) {

        ProductResponse product = fetchProduct(cart.getProductId());

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

        fetchProductForAction(request.getProductId());

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

        evictCartCache(userId);

        return toSaveResponse(savedCart);
    }

    private CartSaveResponse toSaveResponse(Cart cart) {

        ProductResponse product = fetchProduct(cart.getProductId());

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

        fetchProductForAction(cart.getProductId());

        cart.updateQuantity(request.getQuantity());
        Cart updated = cartRepository.save(cart);

        evictCartCache(userId);

        return toUpdateQuantityResponse(updated);

    }

    private CartUpdateQuantityResponse toUpdateQuantityResponse(Cart cart) {

        ProductResponse product = fetchProduct(cart.getProductId());

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

        evictCartCache(userId);
    }

    @Override
    public void clearCart(CartClearEvent event) {
        if (event == null || event.cartId() == null) {
            throw new BizException(CartErrorCode.INVALID_CLEAR_EVENT);
        }

        log.info("[clearCart] 이벤트 수신. cartId: {}", event.cartId());

        Cart cart = cartRepository.findById(event.cartId()).orElse(null);

        if (cart == null) {
            log.warn("[clearCart] Cart를 찾을 수 없음. cartId: {}", event.cartId());
            return;
        }

        UUID userId = cart.getUserId();
        log.info("[clearCart] Cart 찾음. userId: {}", userId);

        try {
            cartRepository.deleteAllByIdIn(List.of(event.cartId()));
            log.info("[clearCart] DB 삭제 완료. cartId: {}", event.cartId());

            evictCartCache(userId);
            log.info("[clearCart] 캐시 삭제 완료. userId: {}", userId);

        } catch (Exception e) {
            log.error("[clearCart] DB 삭제 실패. cartId: {}, userId: {}", event.cartId(), userId, e);
            throw e;
        }

        }

    private void evictCartCache(UUID userId) {
        String cacheKey = CART_CACHE_PREFIX + userId;
        redisTemplate.delete(cacheKey);
    }
}