package com.destiny.userservice.infrastructure.security.jwt;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class JwtProperties {
    private final String secretKey;
    private final long accessExpirationMillis;
    private final long refreshExpirationMillis;
    private final String accessHeaderName;
    private final String refreshCookieName;
    private final String headerPrefix;
    private final String accessSubject;
    private final String refreshSubject;

    public JwtProperties(
        @Value("${jwt.secretKey}") String secretKey,
        @Value("${jwt.access-expiration-millis}") long accessExpirationMillis,
        @Value("${jwt.refresh-expiration-millis}") long refreshExpirationMillis,
        @Value("${jwt.access-header-name}") String accessHeaderName,
        @Value("${jwt.refresh-cookie-name}") String refreshCookieName,
        @Value("${jwt.header-prefix}") String headerPrefix,
        @Value("${jwt.access-subject}") String accessSubject,
        @Value("${jwt.refresh-subject}") String refreshSubject
    ) {
        this.secretKey = secretKey;
        this.accessExpirationMillis = accessExpirationMillis;
        this.refreshExpirationMillis = refreshExpirationMillis;
        this.accessHeaderName = accessHeaderName;
        this.refreshCookieName = refreshCookieName;
        this.headerPrefix = headerPrefix;
        this.accessSubject = accessSubject;
        this.refreshSubject = refreshSubject;
    }
}
