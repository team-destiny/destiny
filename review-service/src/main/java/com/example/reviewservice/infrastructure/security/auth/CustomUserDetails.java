package com.example.reviewservice.infrastructure.security.auth;

import com.auth0.jwt.interfaces.DecodedJWT;
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
        customUserDetails.userId = UUID.fromString(decodedJwt.getClaim("userId").asString());
        customUserDetails.username = decodedJwt.getClaim("username").asString();
        customUserDetails.email = decodedJwt.getClaim("email").asString();
        customUserDetails.accessJwt = decodedJwt.getClaim("accessJwt").asString();
        customUserDetails.userRole = decodedJwt.getClaim("role").asString();
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
