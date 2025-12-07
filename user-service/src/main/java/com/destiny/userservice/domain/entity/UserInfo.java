package com.destiny.userservice.domain.entity;

import com.destiny.global.entity.BaseEntity;
import com.destiny.userservice.presentation.dto.request.UserUpdateRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;

@Entity
@Table(name = "p_user_info")
@Getter
public class UserInfo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;

    @OneToOne
    @MapsId           // User의 PK를 그대로 공유
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, unique = true)
    private String nickname;
    @Column(nullable = false)
    private String phone;

    private String zipCode;
    private String address1;
    private String address2;
    private LocalDate birth;

    // TODO : point, membershipGrade > 추후 분리 가능성 있음
    private Long point;
    @Enumerated(EnumType.STRING)
    private MembershipGrade membershipGrade;
    private Long totalPrice;

    public  UserInfo() {}
    public static UserInfo createUserInfo(
        User user,
        String nickname,
        String phone,
        String zipCode,
        String address1,
        String address2,
        LocalDate birth) {
        UserInfo userInfo = new UserInfo();

        userInfo.user = user;
        userInfo.nickname = nickname;
        userInfo.phone = phone;
        userInfo.zipCode = zipCode;
        userInfo.address1 = address1;
        userInfo.address2 = address2;
        userInfo.birth = birth;
        userInfo.membershipGrade = MembershipGrade.WELCOME;

        return userInfo;
    }

    public void updateProfile(UserUpdateRequest request) {
        if (request.nickname() != null) {
            this.nickname = request.nickname();
        }
        if (request.phone() != null) {
            this.phone = request.phone();
        }
        if (request.zipCode() != null) {
            this.zipCode = request.zipCode();
        }
        if (request.address1() != null) {
            this.address1 = request.address1();
        }
        if (request.address2() != null) {
            this.address2 = request.address2();
        }
    }
}
