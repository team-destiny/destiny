package com.destiny.userservice.infrastructure.repository;

import com.destiny.global.exception.BizException;
import com.destiny.userservice.domain.entity.QUser;
import com.destiny.userservice.domain.entity.User;
import com.destiny.userservice.domain.entity.UserRole;
import com.destiny.userservice.domain.repository.UserRepository;
import com.destiny.userservice.presentation.advice.UserErrorCode;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class UserJpaRepositoryAdaptor implements UserRepository {
    private final UserJpaRepository userJpaRepository;
    private final JPAQueryFactory queryFactory;

    public User save(User user) {
        return userJpaRepository.save(user);
    }

    @Override
    public User findById(UUID userId) {
        return userJpaRepository.findById(userId)
            .orElseThrow(() -> new BizException(UserErrorCode.USER_NOT_FOUND));
    }

    @Override
    public boolean existsByUsernameAndDeletedAtIsNull(String username) {
        return userJpaRepository.existsByUsernameAndDeletedAtIsNull(username);
    }

    @Override
    public User findByUsername(String username) {
        return userJpaRepository.findByUsername(username)
            .orElseThrow(() -> new BizException(UserErrorCode.INVALID_LOGIN_CREDENTIALS));
    }

    @Override
    public boolean existsByUserIdAndDeletedAtIsNull(UUID userId) {
        return userJpaRepository.existsByUserIdAndDeletedAtIsNull(userId);
    }

    @Override
    public Page<User> searchUsers(boolean deleted, UserRole userRole, String searchType,
        String keyword, Pageable pageable) {

        QUser user = QUser.user;
        BooleanBuilder builder = new BooleanBuilder();

        // deleted 필터 (deletedAt null / not null)
        builder.and(deleted ? user.deletedAt.isNotNull() : user.deletedAt.isNull());

        // userRole 필터
        if (userRole != null) {
            builder.and(user.userRole.eq(userRole));
        }

        // search (username / email / nickname / phone)
        if (StringUtils.hasText(keyword) && StringUtils.hasText(searchType)) {
            switch (searchType) {
                case "USERNAME" -> builder.and(user.username.containsIgnoreCase(keyword));
                case "EMAIL"    -> builder.and(user.email.containsIgnoreCase(keyword));
                case "NICKNAME" -> builder.and(user.userInfo.nickname.containsIgnoreCase(keyword));
                case "PHONE"    -> builder.and(user.userInfo.phone.contains(keyword));
                default -> { /* 알 수 없는 searchType 이면 검색 조건 추가 안함 */ }
            }
        }

        JPAQuery<User> query = queryFactory
            .selectFrom(user)
            .where(builder) // join 적용
            .orderBy(getOrderSpecifiers(pageable, user));

        long total = query.fetchCount();

        List<User> content = query
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        return new PageImpl<>(content, pageable, total);
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Pageable pageable, QUser user) {
        return pageable.getSort().stream()
            .map(order -> {
                PathBuilder<User> pathBuilder = new PathBuilder<>(User.class, "user");
                return new OrderSpecifier(
                    order.isAscending() ? Order.ASC : Order.DESC,
                    pathBuilder.get(order.getProperty())
                );
            })
            .toArray(OrderSpecifier[]::new);
    }

    @Override
    public User findByUserIdAndDeletedAtIsNull(UUID userId) {
        return userJpaRepository.findByUserIdAndDeletedAtIsNull(userId)
            .orElseThrow(() -> new BizException(UserErrorCode.USER_NOT_FOUND));
    }



}
