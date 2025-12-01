package com.destiny.userservice.presentation.dto.response;

import com.destiny.userservice.domain.entity.UserInfo;
import java.time.LocalDate;

public record UserInfoResponse(
    String nickname,
    String phone,
    String zipcode,
    String address1,
    String address2,
    LocalDate birth) {

    public static UserInfoResponse from(UserInfo info) {
        if (info == null) return null;
        return new UserInfoResponse(
            info.getNickname(),
            info.getPhone(),
            info.getZipCode(),
            info.getAddress1(),
            info.getAddress2(),
            info.getBirth()
        );
    }
}
