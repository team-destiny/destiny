package com.destiny.notificationservice.infrastructure.repository;

import com.destiny.notificationservice.domain.model.BrandNotificationChannel;
import com.destiny.notificationservice.domain.repository.NotificationChannelRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationChannelRepositoryImpl implements NotificationChannelRepository {

    private final NotificationChannelJpaRepository jpaRepository;

    @Override
    public Optional<BrandNotificationChannel> findByBrandId(UUID brandId) {
        return jpaRepository.findByBrandId(brandId);
    }
}
