package com.destiny.userservice.infrastructure.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.destiny.userservice.domain.entity.User;
import java.time.Instant;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenGenerator {

    private final JwtProperties jwtProperties;
    public JwtTokenGenerator(JwtProperties jwtProperties) {this.jwtProperties = jwtProperties;}

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
}
