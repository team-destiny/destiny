package com.destiny.sagaorchestrator.application.service;

import com.destiny.global.code.CommonErrorCode;
import com.destiny.global.exception.BizException;
import com.destiny.sagaorchestrator.domain.entity.SagaDlqMessage;
import com.destiny.sagaorchestrator.domain.entity.SagaState;
import com.destiny.sagaorchestrator.domain.repository.SagaDlqRepository;
import com.destiny.sagaorchestrator.domain.repository.SagaRepository;
import com.destiny.sagaorchestrator.infrastructure.auth.CustomUserDetails;
import com.destiny.sagaorchestrator.presentation.dto.response.SagaDlqResponse;
import com.destiny.sagaorchestrator.presentation.dto.response.SagaResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SagaService {

    private final SagaRepository sagaRepository;
    private final SagaDlqRepository sagaDlqRepository;

    public List<SagaResponse> sagaLogs(CustomUserDetails customUserDetails) {

        isAdmin(customUserDetails);

        List<SagaState> logs = sagaRepository.findAll();

        return logs.stream()
            .map(SagaResponse::from)
            .toList();
    }

    public List<SagaDlqResponse> dlqLogs(CustomUserDetails customUserDetails) {

        isAdmin(customUserDetails);

        List<SagaDlqMessage> logs = sagaDlqRepository.findAll();

        return logs.stream()
            .map(SagaDlqResponse::from)
            .toList();
    }

    private void isAdmin(CustomUserDetails customUserDetails) {

        if (!customUserDetails.getUserRole().equalsIgnoreCase("MASTER")) {
            throw new BizException(CommonErrorCode.ACCESS_DENIED);
        }
    }
}
