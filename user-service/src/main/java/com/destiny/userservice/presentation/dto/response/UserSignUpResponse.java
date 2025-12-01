package com.destiny.userservice.presentation.dto.response;

import com.destiny.userservice.domain.entity.User;
import com.destiny.userservice.domain.entity.UserRole;
import java.util.UUID;

public record UserSignUpResponse(
    UUID userId,
    UserRole userRole,
    String username,
    UserInfoResponse userInfo
) {

    public static UserSignUpResponse of(User user) {
            return new UserSignUpResponse(user.getUserId()
                , user.getUserRole()
                , user.getUsername()
                , UserInfoResponse.from(user.getUserInfo()));
        }
    }