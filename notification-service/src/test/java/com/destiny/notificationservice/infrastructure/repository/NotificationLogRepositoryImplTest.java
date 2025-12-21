package com.destiny.notificationservice.infrastructure.repository;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.destiny.notificationservice.domain.model.BrandNotificationLog;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class NotificationLogRepositoryImplTest {

    @Mock
    private NotificationLogJpaRepository jpaRepository;

    @InjectMocks
    private NotificationLogRepositoryImpl repository;

    @Test
    @DisplayName("save 호출 시 JPA save가 실행되는지 확인")
    void save() {
        // given
        BrandNotificationLog logEntity = BrandNotificationLog.builder().build();

        // when
        repository.save(logEntity);

        // then
        verify(jpaRepository, times(1)).save(logEntity);
    }

    @Test
    @DisplayName("검색: 브랜드ID + 상태 둘 다 있을 때 findByBrandIdAndStatus 호출 확인")
    void findAllBySearch_both() {
        UUID brandId = UUID.randomUUID();
        String status = "SUCCESS";
        Pageable pageable = PageRequest.of(0, 10);

        repository.findAllBySearch(brandId, status, pageable);

        verify(jpaRepository, times(1)).findByBrandIdAndStatus(eq(brandId), eq(status),
            eq(pageable));
    }

    @Test
    @DisplayName("검색: 브랜드ID만 있을 때 findByBrandId 호출 확인")
    void findAllBySearch_brandOnly() {
        UUID brandId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);

        repository.findAllBySearch(brandId, null, pageable);

        verify(jpaRepository, times(1)).findByBrandId(eq(brandId), eq(pageable));
    }

    @Test
    @DisplayName("검색: 상태만 있을 때 findByStatus 호출 확인")
    void findAllBySearch_statusOnly() {
        String status = "FAIL";
        Pageable pageable = PageRequest.of(0, 10);

        repository.findAllBySearch(null, status, pageable);

        verify(jpaRepository, times(1)).findByStatus(eq(status), eq(pageable));
    }

    @Test
    @DisplayName("검색: 조건 없을 때 findAll 호출 확인")
    void findAllBySearch_none() {
        Pageable pageable = PageRequest.of(0, 10);

        repository.findAllBySearch(null, null, pageable);

        verify(jpaRepository, times(1)).findAll(eq(pageable));
    }
}