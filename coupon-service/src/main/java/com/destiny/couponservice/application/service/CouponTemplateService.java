package com.destiny.couponservice.application.service;

import com.destiny.couponservice.presentation.dto.request.CouponTemplateCreateRequest;
import com.destiny.couponservice.presentation.dto.request.CouponTemplateSearchRequest;
import com.destiny.couponservice.presentation.dto.request.CouponTemplateUpdateRequest;
import com.destiny.couponservice.presentation.dto.response.CouponTemplateCreateResponse;
import com.destiny.couponservice.presentation.dto.response.CouponTemplateDetailResponse;
import com.destiny.couponservice.presentation.dto.response.CouponTemplateListItemResponse;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CouponTemplateService {

    CouponTemplateCreateResponse create(CouponTemplateCreateRequest request);

    CouponTemplateDetailResponse getTemplate(UUID templateId);

    Page<CouponTemplateListItemResponse> search(CouponTemplateSearchRequest request,
        Pageable pageable);

    CouponTemplateDetailResponse update(UUID templateId, CouponTemplateUpdateRequest request);

    void delete(UUID templateId);

}
