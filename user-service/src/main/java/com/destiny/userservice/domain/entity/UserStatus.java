package com.destiny.userservice.domain.entity;

public enum UserStatus {
    ACTIVE("회원 활성화"),
    INACTIVE("회원 비활성화"),
    DELETED("회원 탈퇴");

    private final String description;
    UserStatus(String description) {this.description = description;}
    public String getDescription() {return this.description;}
}
