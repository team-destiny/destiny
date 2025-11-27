package com.gbg.brandservice.infrastructure.repository;

import com.gbg.brandservice.domain.entity.Brand;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandJpaRepository extends JpaRepository<Brand, UUID> {

}
