package com.destiny.orderservice.infrastructure.jwt;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class JwtProperties {

    private final String accessHeaderName;
    private final String headerPrefix;
    private final String accessSubject;

    public JwtProperties(
        @Value("${jwt.access-header-name}") String accessHeaderName,
        @Value("${jwt.header-prefix}") String headerPrefix,
        @Value("${jwt.access-subject}") String accessSubject
    ) {
        this.accessHeaderName = accessHeaderName;
        this.headerPrefix = headerPrefix;
        this.accessSubject = accessSubject;
    }
}
