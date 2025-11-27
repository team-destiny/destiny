package com.gbg.brandservice.presentation.controller;

import com.gbg.brandservice.application.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;



}
