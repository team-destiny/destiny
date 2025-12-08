package com.destiny.userservice.presentation.dto.request;

import com.destiny.userservice.domain.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MasterSignUpRequest (
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

    String adminToken
) {

}