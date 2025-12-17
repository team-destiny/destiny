package com.destiny.paymentservice.infrastructure.security.config;

import com.destiny.paymentservice.infrastructure.security.auth.CustomAccessDeniedHandler;
import com.destiny.paymentservice.infrastructure.security.auth.CustomAuthenticationEntryPoint;
import com.destiny.paymentservice.infrastructure.security.filter.JwtAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // 인증 안 된 사용자가 api 접근했을 때(401) 응답 처리
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    // 인증된 사용자가 권한이 부족할 때(403) 응답 처리
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(csrf -> csrf.disable());
//        httpSecurity.csrf(csrf -> csrf.ignoringRequestMatchers("/v1/payments/tosspayments/**"));
        httpSecurity.formLogin(form -> form.disable());
        httpSecurity.httpBasic(basic -> basic.disable());
        httpSecurity.logout(logout -> logout.disable());
        httpSecurity.sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 예외 처리 커스터마이징
        httpSecurity.exceptionHandling(handler -> handler
            .authenticationEntryPoint(customAuthenticationEntryPoint)
            .accessDeniedHandler(customAccessDeniedHandler));

        // JWT 필터 등록 (SecurityContext에 Authentication 세팅)
        httpSecurity.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);

        httpSecurity.authorizeHttpRequests(authorize -> {
            authorize.requestMatchers("/v1/payments/checkout").permitAll();
            authorize.requestMatchers("/v1/payments/tosspayments/**").permitAll();
            authorize.requestMatchers("/v1/payments/portone/**").permitAll();
            authorize.requestMatchers("/v1/payments/bootpay/**").permitAll();
            authorize.requestMatchers("/actuator/**").permitAll();
            authorize.anyRequest().authenticated();
        });

        return httpSecurity.build();
    }

}
