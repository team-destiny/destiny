package com.destiny.couponservice.presentation.controller;

import com.destiny.couponservice.application.service.CouponTemplateService;
import com.destiny.couponservice.presentation.dto.request.CouponTemplateCreateRequest;
import com.destiny.couponservice.presentation.dto.request.CouponTemplateSearchRequest;
import com.destiny.couponservice.presentation.dto.request.CouponTemplateUpdateRequest;
import com.destiny.couponservice.presentation.dto.response.CouponTemplateCreateResponse;
import com.destiny.couponservice.presentation.dto.response.CouponTemplateGetResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/coupon-templates")
public class CouponTemplateController {

    private final CouponTemplateService couponTemplateService;


    @PostMapping
    public ResponseEntity<CouponTemplateCreateResponse> createTemplate(
        @Valid @RequestBody CouponTemplateCreateRequest request
    ) {
        CouponTemplateCreateResponse response = couponTemplateService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/{templateId}")
    public ResponseEntity<CouponTemplateGetResponse> getTemplate(
        @PathVariable UUID templateId
    ) {
        CouponTemplateGetResponse response = couponTemplateService.getTemplate(templateId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<CouponTemplateGetResponse>> searchTemplates(
        @ModelAttribute CouponTemplateSearchRequest req,
        Pageable pageable
    ) {
        return ResponseEntity.ok(couponTemplateService.search(req, pageable));
    }

    @PatchMapping("/{templateId}")
    public ResponseEntity<CouponTemplateGetResponse> updateTemplate(
        @PathVariable UUID templateId,
        @Valid @RequestBody CouponTemplateUpdateRequest request
    ) {
        CouponTemplateGetResponse response = couponTemplateService.update(templateId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{templateId}")
    public ResponseEntity<Void> deleteTemplate(
        @PathVariable UUID templateId
    ) {
        couponTemplateService.delete(templateId);
        return ResponseEntity.noContent().build();
    }
}
