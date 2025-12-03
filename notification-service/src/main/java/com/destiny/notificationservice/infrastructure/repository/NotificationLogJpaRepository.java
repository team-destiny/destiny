package com.destiny.notificationservice.infrastructure.repository;

import com.destiny.notificationservice.domain.model.BrandNotificationLog;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationLogJpaRepository extends JpaRepository<BrandNotificationLog, UUID> {

    Page<BrandNotificationLog> findByBrandId(UUID brandId, Pageable pageable);

    Page<BrandNotificationLog> findByBrandIdAndStatus(UUID brandId, String status, Pageable pageable);

    Page<BrandNotificationLog> findByStatus(String status, Pageable pageable);
}
