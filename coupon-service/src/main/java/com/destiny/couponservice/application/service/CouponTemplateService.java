package com.destiny.couponservice.application.service;

import com.destiny.couponservice.presentation.dto.request.CouponTemplateCreateRequest;
import com.destiny.couponservice.presentation.dto.request.CouponTemplateSearchRequest;
import com.destiny.couponservice.presentation.dto.response.CouponTemplateCreateResponse;
import com.destiny.couponservice.presentation.dto.response.CouponTemplateGetResponse;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CouponTemplateService {

    CouponTemplateCreateResponse create(CouponTemplateCreateRequest request);

    CouponTemplateGetResponse getTemplate(UUID templateId);

    Page<CouponTemplateGetResponse> search(CouponTemplateSearchRequest request, Pageable pageable);

}
