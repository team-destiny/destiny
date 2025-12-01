package com.destiny.userservice.domain.repository;

import com.destiny.userservice.domain.entity.User;
import java.util.UUID;

public interface UserRepository {
    User save(User user);
    User findById(UUID userId);
    boolean existsByUsernameAndDeletedAtIsNull(String username);
}
