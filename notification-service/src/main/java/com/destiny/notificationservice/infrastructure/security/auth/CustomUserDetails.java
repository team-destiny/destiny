package com.destiny.notificationservice.infrastructure.security.auth;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.destiny.global.code.CommonErrorCode;
import com.destiny.global.exception.BizException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class CustomUserDetails implements UserDetails {

    private UUID userId;
    private String username;
    private String email;
    private String accessJwt;
    private String userRole;

    public CustomUserDetails() {
    }

    public static CustomUserDetails of(DecodedJWT decodedJwt) {
        CustomUserDetails customUserDetails = new CustomUserDetails();
        String userIdStr = decodedJwt.getClaim("userId").asString();
        if (userIdStr == null) {
            throw new BizException(CommonErrorCode.MISSING_PARAMETER);
        }

        try {
            customUserDetails.userId = UUID.fromString(userIdStr);
        } catch (IllegalArgumentException ex) {
            throw new BizException(CommonErrorCode.MISSING_PARAMETER);
        }

        customUserDetails.username = decodedJwt.getClaim("username").asString();
        if (customUserDetails.username == null) {
            throw new BizException(CommonErrorCode.MISSING_PARAMETER);
        }
        customUserDetails.email = decodedJwt.getClaim("email").asString();
        if (customUserDetails.email == null) {
            throw new BizException(CommonErrorCode.MISSING_PARAMETER);
        }
        customUserDetails.accessJwt = decodedJwt.getToken();
        if (customUserDetails.accessJwt == null || customUserDetails.accessJwt.isBlank()) {
            throw new BizException(CommonErrorCode.MISSING_PARAMETER);
        }

        customUserDetails.userRole = decodedJwt.getClaim("userRole").asString();
        if (customUserDetails.userRole == null) {
            throw new BizException(CommonErrorCode.MISSING_PARAMETER);
        }
        return customUserDetails;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + userRole));
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
