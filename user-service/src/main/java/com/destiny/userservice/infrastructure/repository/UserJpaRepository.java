package com.destiny.userservice.infrastructure.repository;

import com.destiny.userservice.domain.entity.User;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, UUID> {
    boolean existsByUsernameAndDeletedAtIsNull(String username);
}
