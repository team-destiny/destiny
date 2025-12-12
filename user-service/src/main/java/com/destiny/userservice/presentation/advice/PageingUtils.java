package com.destiny.userservice.presentation.advice;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageingUtils {
    private static final int DEFAULT_SIZE = 10;
    private static final int[] ALLOWED_SIZES = {10, 30, 50};
    private static final int DEFAULT_OFFSET = 0;

    public static Pageable createPageable(int page, int size, boolean isDescending) {

        // 페이징 정책
        if (!isAllowedSize(size)) {
            size = DEFAULT_SIZE;
        }

        // 정렬 정책
        Sort sort = createSort(isDescending);

        return PageRequest.of(page, size, sort);
    }

    // 페이징 정책. 10, 30, 50 단위로 페이징 가능
    private static boolean isAllowedSize(int size) {
        for (int allowed : ALLOWED_SIZES) {
            if (allowed == size) return true;
        }
        return false;
    }

    // 정렬 정책. 기본값: 생성자, 내림차순
    private static Sort createSort(boolean isDescending) {
        String field = "createdAt";
        return Sort.by(isDescending ? Sort.Direction.DESC : Sort.Direction.ASC, field);
    }
}
