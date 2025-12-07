package com.destiny.userservice.presentation.advice;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageingUtils {
    private static final int DEFAULT_SIZE = 10;
    private static final int[] ALLOWED_SIZES = {10, 30, 50};
    private static final int DEFAULT_OFFSET = 0;

    public static Pageable createPageable(int size, String sortBy, boolean isDescending) {

        // 페이징 정책
        if (!isAllowedSize(size)) {
            size = DEFAULT_SIZE;
        }

        // 정렬 정책
        Sort sort = createSort(sortBy,isDescending);

        return PageRequest.of(DEFAULT_OFFSET, size, sort);
    }

    // 페이징 정책. 10, 30, 50 단위로 페이징 가능
    private static boolean isAllowedSize(int size) {
        for (int allowed : ALLOWED_SIZES) {
            if (allowed == size) return true;
        }
        return false;
    }

    // 정렬 정책. 기본값: 생성자, 내림차순
    private static Sort createSort(String sortBy, boolean isDescending) {
        String field = "createdAt";
        if (sortBy != null && sortBy.equals("updatedAt")) {
            field = "updatedAt";
        }
        return Sort.by(isDescending ? Sort.Direction.DESC : Sort.Direction.ASC, field);
    }
}
