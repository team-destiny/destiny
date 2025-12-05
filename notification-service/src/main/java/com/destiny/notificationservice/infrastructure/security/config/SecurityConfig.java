package com.destiny.notificationservice.infrastructure.security.config;


import com.destiny.notificationservice.infrastructure.security.auth.CustomAccessDeniedHandler;
import com.destiny.notificationservice.infrastructure.security.auth.CustomAuthenticationEntryPoint;
import com.destiny.notificationservice.infrastructure.security.filter.JwtAuthorizationFilter;
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
@EnableWebSecurity // Spring Security 지원
// Controller, Service, Repo에서 권한/인증 감사 어노테이선 사용 허용
// ex) @PreAuthorize("hasRole('MASTER')")
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
        httpSecurity.addFilterBefore(jwtAuthorizationFilter,
            UsernamePasswordAuthenticationFilter.class);

        httpSecurity.authorizeHttpRequests(authorize -> {
            authorize
                .requestMatchers("/actuator/**").permitAll()
                // 로그 조회: MASTER, PARTNER
                .requestMatchers("/v1/brand-notifications/logs")
                .hasAnyRole("MASTER", "PARTNER")
                // 나머지 알림 발송 API: 내부 호출이면 permitAll
                .requestMatchers("/v1/brand-notifications/**").permitAll()

                .anyRequest().permitAll();
        });

        return httpSecurity.build();
    }
}
