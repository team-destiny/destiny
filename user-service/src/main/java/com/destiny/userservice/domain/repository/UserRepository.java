package com.destiny.userservice.domain.repository;

import com.destiny.userservice.domain.entity.User;
import com.destiny.userservice.domain.entity.UserRole;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepository {
    User save(User user);
    User findById(UUID userId);
    boolean existsByUsernameAndDeletedAtIsNull(String username);
    User findByUsername(String username);
    boolean existsByUserIdAndDeletedAtIsNull(UUID userId);
    Page<User> searchUsers(boolean deleted, UserRole userRole, String searchType, String keyword, Pageable pageable);
    User findByUserIdAndDeletedAtIsNull(UUID userId);
}
