package com.example.reviewservice.infrastructure.security.auth;

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

    public CustomUserDetails() {};

    public static CustomUserDetails of(DecodedJWT decodedJwt) {
        CustomUserDetails customUserDetails = new CustomUserDetails();
        String userIdStr = decodedJwt.getClaim("userId").asString();
        if (userIdStr == null) {
            throw new BizException(CommonErrorCode.MISSING_PARAMETER);
        }
        customUserDetails.userId = UUID.fromString(userIdStr);
        customUserDetails.username = decodedJwt.getClaim("username").asString();
        customUserDetails.email = decodedJwt.getClaim("email").asString();
        customUserDetails.accessJwt = decodedJwt.getToken();
        customUserDetails.userRole = decodedJwt.getClaim("userRole").asString();
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
