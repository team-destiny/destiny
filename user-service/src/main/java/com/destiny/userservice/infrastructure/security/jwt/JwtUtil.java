package com.destiny.userservice.infrastructure.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.destiny.global.code.CommonErrorCode;
import com.destiny.global.exception.BizException;
import com.destiny.userservice.domain.entity.User;
import com.destiny.userservice.presentation.advice.UserErrorCode;
import java.time.Instant;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private final JwtProperties jwtProperties;
    public JwtUtil(JwtProperties jwtProperties) {this.jwtProperties = jwtProperties;}

    private Algorithm algorithm() {
        return Algorithm.HMAC256(jwtProperties.getSecretKey());
    }

    public String generateAccessToken(User user) {
        Objects.requireNonNull(user, "user must not be null");

        Instant now = Instant.now();
        return JWT.create()
            .withSubject(jwtProperties.getAccessSubject())
            .withIssuedAt(now)
            .withExpiresAt(now.plusMillis(jwtProperties.getAccessExpirationMillis()))
            .withClaim("userId", user.getUserId().toString())
            .withClaim("username", user.getUsername())
            .withClaim("email", user.getEmail())
            .withClaim("userRole", user.getUserRole().toString())
            .sign(algorithm());
    }

    public String generateRefreshToken(String userId) {
        Objects.requireNonNull(userId, "user must not be null");

        Instant now = Instant.now();
        return JWT.create()
            .withSubject(jwtProperties.getRefreshSubject())
            .withIssuedAt(now)
            .withExpiresAt(now.plusMillis(jwtProperties.getRefreshExpirationMillis()))
            .withClaim("userId", userId)
            .sign(algorithm());
    }

    public DecodedJWT verifyRefreshToken(String refreshToken) {
        DecodedJWT decodedRefreshJwt;
        try {
            decodedRefreshJwt = JWT.require(algorithm())
                .build()
                .verify(refreshToken);
        } catch (JWTVerificationException e) {
            throw new BizException(UserErrorCode.USER_TOKEN_INVALID);
        } catch (IllegalArgumentException e) {
            throw new BizException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
        return decodedRefreshJwt;
    }
}
