package com.destiny.userservice.infrastructure.repository;

import com.destiny.global.exception.BizException;
import com.destiny.userservice.domain.entity.User;
import com.destiny.userservice.domain.exception.UserErrorCode;
import com.destiny.userservice.domain.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserJpaRepositoryAdaptor implements UserRepository {
    private final UserJpaRepository userJpaRepository;

    public User save(User user) {
        return userJpaRepository.save(user);
    }

    @Override
    public User findById(UUID userId) {
        return userJpaRepository.findById(userId)
            // TODO : userException 추가
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

}
