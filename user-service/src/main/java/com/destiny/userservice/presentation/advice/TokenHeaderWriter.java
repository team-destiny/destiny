package com.destiny.userservice.presentation.advice;

import com.destiny.userservice.infrastructure.security.jwt.JwtProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
// refreshToken HttpOnly 쿠키에 저장
public class TokenHeaderWriter {

    private final JwtProperties jwtProperties;

    public void accessTokenWrite(HttpServletResponse servletResponse, String accessToken) {
        String headerValue = jwtProperties.getHeaderPrefix() + " " + accessToken;
        servletResponse.setHeader(jwtProperties.getAccessHeaderName(), headerValue);
    }

    public void refreshTokenWrite(HttpServletResponse servletResponse, String refreshToken) {
        Cookie cookie = new Cookie(jwtProperties.getRefreshCookieName(), refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(jwtProperties.isRefreshCookieSecure());
        cookie.setPath("/");
        cookie.setMaxAge((int) (jwtProperties.getRefreshExpirationMillis() / 1000));

        servletResponse .addCookie(cookie);
    }

    public void expire(HttpServletResponse response) {
        Cookie cookie = new Cookie(jwtProperties.getRefreshCookieName(), "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);  // 바로 만료
        response.addCookie(cookie);
    }
}
