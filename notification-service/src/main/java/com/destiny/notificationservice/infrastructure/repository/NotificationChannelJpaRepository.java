package com.destiny.notificationservice.infrastructure.repository;

import com.destiny.notificationservice.domain.model.BrandNotificationChannel;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationChannelJpaRepository extends
    JpaRepository<BrandNotificationChannel, UUID> {

    Optional<BrandNotificationChannel> findByBrandId(UUID brandId);
}
