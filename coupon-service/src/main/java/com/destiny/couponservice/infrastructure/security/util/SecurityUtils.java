package com.destiny.couponservice.infrastructure.security.util;

import com.destiny.couponservice.infrastructure.security.auth.CustomUserDetails;
import com.destiny.global.code.CommonErrorCode;
import com.destiny.global.exception.BizException;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BizException(CommonErrorCode.UNAUTHORIZED);
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getUserId();
        }

        throw new BizException(CommonErrorCode.UNAUTHORIZED);
    }
}
