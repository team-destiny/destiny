package com.destiny.userservice.presentation.dto.response;

import com.destiny.userservice.domain.entity.MembershipGrade;
import com.destiny.userservice.domain.entity.User;
import com.destiny.userservice.domain.entity.UserRole;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserGetResponse(
    UUID userId,
    UserRole userRole,
    String username,
    String name,
    String phone,
    String email,
    String zipCode,
    String address1,
    String address2,
    LocalDateTime birth,
    Long point,
    MembershipGrade membershipGrade
) {
    public static UserGetResponse of(User user) {
        return new UserGetResponse(user.getUserId()
            , user.getUserRole()
            , user.getUsername()
            , user.getName()
            , user.getPhone()
            , user.getEmail()
            , user.getZipCode()
            , user.getAddress1()
            , user.getAddress2()
            , user.getBirth()
            , user.getPoint()
            , user.getMembershipGrade());
    }

}
