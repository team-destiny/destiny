package com.destiny.brandservice.infrastructure.repository;

import com.destiny.brandservice.domain.entity.Brand;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandJpaRepository extends JpaRepository<Brand, UUID> {

}
