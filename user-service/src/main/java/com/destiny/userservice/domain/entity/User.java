package com.destiny.userservice.domain.entity;

import com.destiny.global.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
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
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole userRole;
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
    private UserInfo userInfo;

    public User() {}

    public static User createUser(
        String username,
        String password,
        String email,
        UserRole userRole,
        String nickname,
        String phone,
        String zipCode,
        String address1,
        String address2,
        LocalDate birth) {

        User user = new User();

        user.username = username;
        user.password = password;
        user.email = email;
        user.userRole = (userRole == null ? UserRole.CUSTOMER : userRole);
        user.userStatus = UserStatus.ACTIVE;

        boolean needUserInfo = user.userRole != UserRole.MASTER;

        if (needUserInfo) {
            UserInfo userInfo = UserInfo.createUserInfo(
                user,
                nickname,
                phone,
                zipCode,
                address1,
                address2,
                birth
            );
            user.userInfo = userInfo;
        }

        return user;
    }
}
