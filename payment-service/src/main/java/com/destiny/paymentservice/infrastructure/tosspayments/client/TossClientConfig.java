package com.destiny.paymentservice.infrastructure.tosspayments.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
public class TossClientConfig {

    // (TODO: application.properties에서 TOSS 시크릿 키를 주입받도록 설정)
    @Value("${toss.secret-key:test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6}")
    private String secretKey;

    /**
     * Feign 요청 시마다 Authorization 헤더를 자동으로 추가하는 인터셉터 빈을 정의합니다.
     */
    @Bean
    public RequestInterceptor tossAuthorizationInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                // TOSS API의 Basic 인증 헤더 생성 로직
                // secretKey: 콜론을 Base64 인코딩
                String authString = secretKey + ":";
                String authorizations = "Basic " + Base64.getEncoder().encodeToString(authString.getBytes(StandardCharsets.UTF_8));

                // 요청 템플릿에 헤더를 추가
                template.header("Authorization", authorizations);
                template.header("Content-Type", "application/json");
            }
        };
    }
}