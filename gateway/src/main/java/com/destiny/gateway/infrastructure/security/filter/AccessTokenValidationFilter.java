package com.destiny.gateway.infrastructure.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.destiny.gateway.application.cache.AuthCache;
import com.destiny.gateway.infrastructure.security.jwt.JwtProperties;
import com.destiny.gateway.presentation.advice.GatewayErrorCode;
import com.destiny.gateway.presentation.advice.GatewayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccessTokenValidationFilter implements GlobalFilter, Ordered {

    private final JwtProperties jwtProperties;
    private final AuthCache authCache;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String authorizationHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String accessJwt = resolveAccessToken(authorizationHeader);

        log.info("Access Token 검증 시작: {}", accessJwt);
        
        if (!StringUtils.hasText(accessJwt)) {
            return chain.filter(removeAuthorizationHeaderFromExchange(exchange));
        }

        DecodedJWT decodedAccessJwt;
        try {
            decodedAccessJwt = JWT.require(Algorithm.HMAC256(jwtProperties.getSecretKey()))
                                .build()
                                .verify(accessJwt);
        } catch (JWTVerificationException e) {
            throw new GatewayException(GatewayErrorCode.GATEWAY_TOKEN_INVALID);
        } catch (IllegalArgumentException e) {
            throw new GatewayException(GatewayErrorCode.INTERNAL_SERVER_ERROR);
        }

        String userId = decodedAccessJwt.getClaim("userId").asString();
        if (!StringUtils.hasText(userId)) {
            throw new GatewayException(GatewayErrorCode.GATEWAY_TOKEN_INVALID);
        }

        // 토큰 블랙리스트 검증
        Long blockedTokenMillis = authCache.getToken(userId);
        Long tokenIssuedAtMillis = decodedAccessJwt.getIssuedAtAsInstant().toEpochMilli();
        if (blockedTokenMillis != null && blockedTokenMillis > tokenIssuedAtMillis) {
            throw new GatewayException(GatewayErrorCode.GATEWAY_TOKEN_INVALID);
        }


        log.info("Access Token 검증 완료");
        return chain.filter(exchange);
    }

    /**
     * AccessToken 추출
     */
    private String resolveAccessToken(String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader)) {
            return null;
        }

        if (authorizationHeader.regionMatches(true, 0, jwtProperties.getHeaderPrefix(), 0, jwtProperties.getHeaderPrefix().length())) {
            return authorizationHeader.substring(jwtProperties.getHeaderPrefix().length()).trim();
        }
        return authorizationHeader.trim();
    }

    /**
     * 토큰 없는 경우 익명 요청 처리 (Authorization 헤더 제거)
     */
    private static ServerWebExchange removeAuthorizationHeaderFromExchange(ServerWebExchange exchange) {
        log.info("토큰 헤더 제거");
        ServerHttpRequest mutatedRequest = exchange.getRequest()
            .mutate()
            .headers(headers -> headers.remove(HttpHeaders.AUTHORIZATION))
            .build();
        ServerWebExchange mutatedExchange = exchange.mutate()
            .request(mutatedRequest)
            .build();
        log.info("토큰 헤더 제거 완료");
        return mutatedExchange;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
