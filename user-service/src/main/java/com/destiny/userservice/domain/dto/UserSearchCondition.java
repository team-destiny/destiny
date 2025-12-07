package com.destiny.userservice.domain.dto;

import com.destiny.userservice.domain.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchCondition {
    private Boolean deleted;          // null 이면 필터 안 함
    private UserRole userRole;        // null 이면 필터 안 함
    private UserSearchType searchType; // null 이면 검색 안 함
    private String keyword;           // null or blank 이면 검색 안 함

}
