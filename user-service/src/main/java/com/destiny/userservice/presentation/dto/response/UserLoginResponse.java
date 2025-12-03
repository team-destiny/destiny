package com.destiny.userservice.presentation.dto.response;

import com.destiny.userservice.domain.entity.User;
import com.destiny.userservice.domain.entity.UserRole;
import java.util.UUID;

public record UserLoginResponse(
    UUID userId,
    UserRole userRole,
    String username,
    String email,
    UserInfoResponse userInfo
) {
    public static UserLoginResponse of(User user) {
        return new UserLoginResponse(user.getUserId()
            , user.getUserRole()
            , user.getUsername()
            , user.getEmail()
            , UserInfoResponse.from(user.getUserInfo()));
    }
}
