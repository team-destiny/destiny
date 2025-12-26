package com.destiny.sagaorchestrator.presentation.controller;

import com.destiny.sagaorchestrator.application.service.SagaService;
import com.destiny.sagaorchestrator.infrastructure.auth.CustomUserDetails;
import com.destiny.sagaorchestrator.presentation.dto.response.SagaDlqResponse;
import com.destiny.sagaorchestrator.presentation.dto.response.SagaResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/sagas")
public class SagaController {

    private final SagaService sagaService;

    @GetMapping
    public ResponseEntity<List<SagaResponse>> sagaLogs(
        @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        List<SagaResponse> logs = sagaService.sagaLogs(customUserDetails);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(logs);
    }

    @GetMapping("/dlq")
    public ResponseEntity<List<SagaDlqResponse>> dlqLogs(
        @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        List<SagaDlqResponse> logs = sagaService.dlqLogs(customUserDetails);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(logs);
    }

}
