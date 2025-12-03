package com.destiny.notificationservice.domain.model;

import com.destiny.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "p_brand_notification_channel")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BrandNotificationChannel extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID brandId;

    @Column(name = "slack_channel", nullable = false, length = 200)
    private String slackUrl;

    @Column(nullable = false)
    private boolean isActive;

    public BrandNotificationChannel(UUID brandId, String slackUrl) {
        this.brandId = brandId;
        this.slackUrl = slackUrl;
        this.isActive = true;
    }
}
