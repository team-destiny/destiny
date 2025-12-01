package com.destiny.userservice.presentation.aop;

import com.destiny.userservice.domain.dto.LoginTokens;
import com.destiny.userservice.presentation.advice.TokenHeaderWriter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class TokenIssueAspect {

    private final TokenHeaderWriter tokenHeaderWriter;

    @Around("@annotation(com.destiny.userservice.presentation.annotation.IssueTokens)")
    public Object  issueTokens(ProceedingJoinPoint joinPoint) throws Throwable {
        log.debug("LoginTokenAspect 실행");

        try {
            // 원래 메서드 실행
            Object result = joinPoint.proceed();

            // ThreadLocal에서 LoginTokens 가져오기
            LoginTokens loginTokens = TokenContextHolder.getTokens();

            if (loginTokens != null) {
                HttpServletResponse servletResponse = getHttpServletResponse();

                if (servletResponse != null) {
                    tokenHeaderWriter.accessTokenWrite(servletResponse, loginTokens.accessToken());
                    tokenHeaderWriter.refreshTokenWrite(servletResponse, loginTokens.refreshToken());
                } else {
                    log.warn("HttpServletResponse를 가져올 수 없습니다");
                }
            } else {
                log.warn("LoginTokens를 가져올 수 없습니다");
            }

            return result;

        } finally {
            // ThreadLocal 정리 (메모리 누수 방지)
            TokenContextHolder.clear();
        }
    }

    /**
     * 현재 요청의 HttpServletResponse 가져오기
     */
    private HttpServletResponse getHttpServletResponse() {
        ServletRequestAttributes attributes =
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getResponse() : null;
    }
}
