package com.destiny.userservice.presentation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public record UserUpdateRequest(
    String nickname,
    @Pattern(regexp="\\d{3}-\\d{4}-\\d{4}") String phone,
    @Email String email,
    String zipCode,
    String address1,
    String address2
) {

}
