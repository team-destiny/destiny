package com.destiny.notificationservice.domain.repository;

import com.destiny.notificationservice.domain.model.BrandNotificationChannel;
import java.util.Optional;
import java.util.UUID;

public interface NotificationChannelRepository {

    Optional<BrandNotificationChannel> findByBrandId(UUID brandId);
}
