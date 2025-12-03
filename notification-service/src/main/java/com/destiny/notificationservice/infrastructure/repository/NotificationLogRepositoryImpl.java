package com.destiny.notificationservice.infrastructure.repository;

import com.destiny.notificationservice.domain.model.BrandNotificationLog;
import com.destiny.notificationservice.domain.repository.NotificationLogRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationLogRepositoryImpl implements NotificationLogRepository {

    private final NotificationLogJpaRepository jpaRepository;

    @Override
    public BrandNotificationLog save(BrandNotificationLog log) {
        return jpaRepository.save(log);
    }

    @Override
    public Page<BrandNotificationLog> findAllBySearch(UUID brandId, String status, Pageable pageable) {
        if (brandId != null && status != null) {
            return jpaRepository.findByBrandIdAndStatus(brandId, status, pageable);
        } else if (brandId != null) {
            return jpaRepository.findByBrandId(brandId, pageable);
        } else if (status != null) {
            return jpaRepository.findByStatus(status, pageable);
        } else {
            return jpaRepository.findAll(pageable);
        }
    }
}
