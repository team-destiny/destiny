package com.destiny.notificationservice.infrastructure.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.destiny.notificationservice.infrastructure.security.auth.CustomUserDetails;
import com.destiny.notificationservice.infrastructure.security.jwt.JwtProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "토큰 권한 인가")
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtProperties jwtProperties;

    private JWTVerifier getVerifier() {
        Algorithm algorithm = Algorithm.HMAC256(jwtProperties.getSecretKey());
        return JWT.require(algorithm).build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain)
        throws ServletException, IOException {

        String authorizationHeader = request.getHeader(jwtProperties.getAccessHeaderName());
        if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith(
            jwtProperties.getHeaderPrefix())) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessJwt = authorizationHeader.substring(jwtProperties.getHeaderPrefix().length())
            .trim();
        DecodedJWT decodedAccessJwt;
        try {
            decodedAccessJwt = getVerifier().verify(accessJwt);
        } catch (Exception e) {
            log.warn("JWT 검증 실패: {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        CustomUserDetails userDetails;
        try {
            userDetails = CustomUserDetails.of(decodedAccessJwt);
        } catch (RuntimeException exception) {
            filterChain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }
}
