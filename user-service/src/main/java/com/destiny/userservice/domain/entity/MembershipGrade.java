package com.destiny.userservice.domain.entity;

public enum MembershipGrade {
    WELCOME("총 구매 금액 0원 이상 10만원 미만"),
    FRIEND("총 구매 금액 10만원 이상 50만원 미만"),
    FAMILY("총 구매 금액 50만원 이상 1,000만원 미만"),
    VIP("총 구매 금액 1,000만원 이상");

    private final String description;
    MembershipGrade(String description) {this.description = description;}
    public String getDescription() {return this.description;}
}
