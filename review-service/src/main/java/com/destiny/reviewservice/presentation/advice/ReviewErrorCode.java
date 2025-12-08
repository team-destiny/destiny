package com.destiny.reviewservice.presentation.advice;

import com.destiny.global.code.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReviewErrorCode implements ResponseCode {
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW_ERROR_001", "리뷰를 찾을 수 없습니다."),
    INVALID_SEARCH_CONDITION(HttpStatus.BAD_REQUEST, "REVIEW_ERROR_002", "productId와 userId는 동시에 사용할 수 없습니다."),
    USER_NOT_ORDER_OWNER(HttpStatus.UNAUTHORIZED, "REVIEW_ERROR_003", "주문한 사용자만 리뷰를 작성할 수 있습니다."),
    ORDER_NOT_REVIEWABLE(HttpStatus.BAD_REQUEST, "REVIEW_ERROR_004", "아직 리뷰를 작성할 수 없는 주문 상태입니다."),
    PRODUCT_NOT_IN_ORDER(HttpStatus.BAD_REQUEST, "REVIEW_ERROR_005", "해당 상품은 주문 내역에 포함되어 있지 않습니다."),
    REVIEW_ALREADY_EXISTS(HttpStatus.CONFLICT, "REVIEW_ERROR_006", "이미 리뷰를 작성한 주문/상품입니다."),
    INVALID_REVIEW_RATING(HttpStatus.BAD_REQUEST, "REVIEW_ERROR_007", "리뷰 평점 형식이 올바르지 않습니다."),
    ORDER_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW_ERROR_008", "주문 정보를 찾을 수 없습니다."),
    PRODUCT_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW_ERROR_009", "상품 정보를 찾을 수 없습니다."),
    ORDER_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "REVIEW_ERROR_010", "주문 서비스 응답이 지연되고 있습니다. 잠시 후 다시 시도해주세요.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;

}
