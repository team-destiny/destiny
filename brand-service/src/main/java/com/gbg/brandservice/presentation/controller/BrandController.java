package com.gbg.brandservice.presentation.controller;

import com.gbg.brandservice.application.service.BrandService;
import com.gbg.brandservice.presentation.dto.request.BrandCreateRequest;
import com.gbg.brandservice.presentation.dto.response.BrandCreateResponse;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/brand-service")
public class BrandController {

    private final BrandService brandService;

    @PostMapping
    public ResponseEntity<BrandCreateResponse> createBrand(
        @RequestBody BrandCreateRequest req
    ) {

        BrandCreateResponse brandCreateResponse = new BrandCreateResponse(
            UUID.randomUUID(),
            req.managerId(),
            req.brandName(),
            LocalDateTime.now(),
            UUID.randomUUID(),
            LocalDateTime.now(),
            UUID.randomUUID()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(brandCreateResponse);
    }

}
