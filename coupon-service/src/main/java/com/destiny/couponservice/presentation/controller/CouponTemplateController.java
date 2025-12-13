package com.destiny.couponservice.presentation.controller;

import com.destiny.couponservice.application.service.CouponTemplateService;
import com.destiny.couponservice.presentation.advice.CouponTemplateSuccessCode;
import com.destiny.couponservice.presentation.dto.request.CouponTemplateCreateRequest;
import com.destiny.couponservice.presentation.dto.request.CouponTemplateSearchRequest;
import com.destiny.couponservice.presentation.dto.request.CouponTemplateUpdateRequest;
import com.destiny.couponservice.presentation.dto.response.CouponTemplateCreateResponse;
import com.destiny.couponservice.presentation.dto.response.CouponTemplateDetailResponse;
import com.destiny.couponservice.presentation.dto.response.CouponTemplateListItemResponse;
import com.destiny.couponservice.presentation.dto.response.CouponTemplateSearchResponse;
import com.destiny.global.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/coupon-templates")
public class CouponTemplateController {

    private final CouponTemplateService couponTemplateService;

    @PostMapping
    public ResponseEntity<ApiResponse<CouponTemplateCreateResponse>> createTemplate(
        @Valid @RequestBody CouponTemplateCreateRequest request
    ) {
        CouponTemplateCreateResponse response = couponTemplateService.create(request);

        return ResponseEntity
            .status(CouponTemplateSuccessCode.COUPON_TEMPLATE_CREATE.getStatus())
            .body(ApiResponse.success(CouponTemplateSuccessCode.COUPON_TEMPLATE_CREATE, response));
    }

    @GetMapping("/{templateId}")
    public ResponseEntity<ApiResponse<CouponTemplateDetailResponse>> getTemplate(
        @PathVariable UUID templateId
    ) {
        CouponTemplateDetailResponse response = couponTemplateService.getTemplate(templateId);

        return ResponseEntity
            .status(CouponTemplateSuccessCode.COUPON_TEMPLATE_GET.getStatus())
            .body(ApiResponse.success(CouponTemplateSuccessCode.COUPON_TEMPLATE_GET, response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CouponTemplateSearchResponse>> searchTemplates(
        @ModelAttribute CouponTemplateSearchRequest req,
        Pageable pageable
    ) {
        Page<CouponTemplateListItemResponse> page = couponTemplateService.search(req, pageable);
        CouponTemplateSearchResponse response = CouponTemplateSearchResponse.from(page);

        return ResponseEntity
            .status(CouponTemplateSuccessCode.COUPON_TEMPLATE_LIST_GET.getStatus())
            .body(
                ApiResponse.success(CouponTemplateSuccessCode.COUPON_TEMPLATE_LIST_GET, response));
    }

    @PutMapping("/{templateId}")
    public ResponseEntity<ApiResponse<CouponTemplateDetailResponse>> updateTemplate(
        @PathVariable UUID templateId,
        @Valid @RequestBody CouponTemplateUpdateRequest request
    ) {
        CouponTemplateDetailResponse response = couponTemplateService.update(templateId, request);

        return ResponseEntity
            .status(CouponTemplateSuccessCode.COUPON_TEMPLATE_UPDATE.getStatus())
            .body(ApiResponse.success(CouponTemplateSuccessCode.COUPON_TEMPLATE_UPDATE, response));
    }

    @DeleteMapping("/{templateId}")
    public ResponseEntity<ApiResponse<Void>> deleteTemplate(
        @PathVariable UUID templateId
    ) {
        couponTemplateService.delete(templateId);

        return ResponseEntity
            .status(CouponTemplateSuccessCode.COUPON_TEMPLATE_DELETE.getStatus())
            .body(ApiResponse.success(CouponTemplateSuccessCode.COUPON_TEMPLATE_DELETE));
    }
}
