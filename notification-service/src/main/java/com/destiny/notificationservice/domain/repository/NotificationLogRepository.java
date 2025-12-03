package com.destiny.notificationservice.domain.repository;

import com.destiny.notificationservice.domain.model.BrandNotificationLog;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationLogRepository {

    BrandNotificationLog save(BrandNotificationLog log);

    // 로그조회
    Page<BrandNotificationLog> findAllBySearch(UUID brandId, String status, Pageable pageable);

}