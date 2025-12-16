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
    private final String refreshHeaderName;
    private final String headerPrefix;
    private final String accessSubject;
    private final String refreshSubject;
    private final boolean isRefreshCookieSecure;

    public JwtProperties(
        @Value("${jwt.secretKey}") String secretKey,
        @Value("${jwt.access-expiration-millis}") long accessExpirationMillis,
        @Value("${jwt.refresh-expiration-millis}") long refreshExpirationMillis,
        @Value("${jwt.access-header-name}") String accessHeaderName,
        @Value("${jwt.refresh-header-name}") String refreshHeaderName,
        @Value("${jwt.header-prefix}") String headerPrefix,
        @Value("${jwt.access-subject}") String accessSubject,
        @Value("${jwt.refresh-subject}") String refreshSubject,
        @Value("${jwt.is-refresh-cookie-secure}") boolean isRefreshCookieSecure
    ) {
        this.secretKey = secretKey;
        this.accessExpirationMillis = accessExpirationMillis;
        this.refreshExpirationMillis = refreshExpirationMillis;
        this.accessHeaderName = accessHeaderName;
        this.refreshHeaderName = refreshHeaderName;
        this.headerPrefix = headerPrefix;
        this.accessSubject = accessSubject;
        this.refreshSubject = refreshSubject;
        this.isRefreshCookieSecure = isRefreshCookieSecure;
    }
}
