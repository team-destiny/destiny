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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "p_brand_notification_log")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BrandNotificationLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = true)
    private UUID brandId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false, length = 10)
    private String status;



    private Integer responseCode;

    @Column(columnDefinition = "TEXT")
    private String responseMessage;

    @Column(length = 50)
    private String errorCode;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @Builder
    public BrandNotificationLog(UUID brandId, String message, String status, Integer responseCode, String responseMessage, String errorCode, String errorMessage) {
        this.brandId = brandId;
        this.message = message;
        this.status = status;
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public static BrandNotificationLog create(UUID brandId, String message, String status) {
        return BrandNotificationLog.builder()
            .brandId(brandId)
            .message(message)
            .status(status)
            .build();
    }


}
