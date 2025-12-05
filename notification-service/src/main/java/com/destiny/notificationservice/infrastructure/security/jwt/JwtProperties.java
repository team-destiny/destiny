package com.destiny.notificationservice.infrastructure.security.jwt;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class JwtProperties {

    private final String secretKey;
    private final String accessHeaderName;
    private final String headerPrefix;

    public JwtProperties(
        @Value("${jwt.secretKey}") String secretKey,
        @Value("${jwt.access-header-name}") String accessHeaderName,
        @Value("${jwt.header-prefix}") String headerPrefix
    ) {
        this.secretKey = secretKey;
        this.accessHeaderName = accessHeaderName;
        this.headerPrefix = headerPrefix;

    }
}
