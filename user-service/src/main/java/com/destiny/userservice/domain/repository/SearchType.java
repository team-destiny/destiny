package com.destiny.userservice.domain.repository;

import java.util.Arrays;

public enum SearchType {
    USERNAME("username"),
    EMAIL("email"),
    NICKNAME("nickname"),
    PHONE("phone");

    private final String value;

    SearchType(String value) {
        this.value = value;
    }

    public static SearchType fromValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Unknown search type: " + value);
        }
        return Arrays.stream(values())
            .filter(type -> type.value.equalsIgnoreCase(value))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                "Unknown search type: " + value
            ));
    }

    @Override
    public String toString() {
        return value;
    }
}
