package com.destiny.notificationservice.infrastructure.repository;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationChannelRepositoryImplTest {

    @Mock
    private NotificationChannelJpaRepository jpaRepository;

    @InjectMocks
    private NotificationChannelRepositoryImpl repository;

    @Test
    @DisplayName("브랜드ID로 조회 시 JPA findByBrandId 호출 확인")
    void findByBrandId() {
        // given
        UUID brandId = UUID.randomUUID();

        // when
        repository.findByBrandId(brandId);

        // then
        verify(jpaRepository, times(1)).findByBrandId(brandId);
    }
}