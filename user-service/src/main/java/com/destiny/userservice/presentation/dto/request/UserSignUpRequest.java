package com.destiny.userservice.presentation.dto.request;

import com.destiny.userservice.domain.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record UserSignUpRequest(
    @NotBlank(message = "id는 필수입니다.")
    @Size(min = 4, max = 10, message = "id는 4-10자 이내여야 합니다.")
    @Pattern(regexp = "^[a-z0-9]+$", message = "id는 영소문자와 숫자만 사용 가능합니다.")
    String username,
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 15, message = "비밀번호는 8-15자 이내여야 합니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*])[a-zA-Z\\d!@#$%^&*]+$",
        message = "비밀번호는 영대소문자, 숫자, 특수문자를 모두 포함해야 합니다.")
    String password,
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    String email,
    UserRole userRole,
    @Size(min = 2, max = 10, message = "닉네임은 2-10자 이내여야 합니다.")
    String nickname,
    @Pattern(regexp="\\d{3}-\\d{4}-\\d{4}")
    String phone,

    String zipcode,
    String address1,
    String address2,
    LocalDate birth,

    String adminToken
) {

}
