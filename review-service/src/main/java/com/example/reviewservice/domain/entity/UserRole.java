package com.example.reviewservice.domain.entity;

public enum UserRole {
    CUSTOMER("회원"),
    PARTNER("업체 담당자"),
    MASTER("관리자");

    private final String description;
    UserRole(String description) {this.description = description;}
    public String getDescription() {return this.description;}
}
