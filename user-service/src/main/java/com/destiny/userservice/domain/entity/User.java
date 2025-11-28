package com.destiny.userservice.domain.entity;

import com.destiny.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;

@Entity
@Table(name = "p_user")
@Getter
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;

    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole userRole;
    private String name;
    @Column(nullable = false)
    private String phone;
    @Column(nullable = false)
    private String email;

    private String zipCode;
    private String address1;
    private String address2;
    private LocalDateTime birth;

    private Double point;
    @Enumerated(EnumType.STRING)
    private MembershipGrade membershipGrade;
    private Long totalPrice;

    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;


}
