package com.destiny.userservice.infrastructure.security.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // Spring Security 지원
// Controller, Service, Repo에서 권한/인증 감사 어노테이선 사용 허용
// ex) @PreAuthorize("hasRole('MASTER')")
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(csrf -> csrf.disable());
        httpSecurity.formLogin(form -> form.disable());
        httpSecurity.httpBasic(basic -> basic.disable());
        httpSecurity.logout(logout -> logout.disable());

        httpSecurity.authorizeHttpRequests(authorize -> {
            authorize.requestMatchers("/v1/auths/**").permitAll();
            authorize.anyRequest().authenticated();
//            authorize.anyRequest().permitAll();

        });

        return httpSecurity.build();
    }
}
