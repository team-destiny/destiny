package com.destiny.sagaorchestrator.presentation.controller;

import com.destiny.sagaorchestrator.application.service.SagaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SagaController {

    private final SagaService sagaService;

    // TODO : 사가 로그 조회 마스터 권한.


}
