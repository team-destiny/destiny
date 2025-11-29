package com.destiny.sagaorchestrator.application.service;

import com.destiny.sagaorchestrator.domain.repository.SagaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SagaService {

    private final SagaRepository sagaRepository;


}
