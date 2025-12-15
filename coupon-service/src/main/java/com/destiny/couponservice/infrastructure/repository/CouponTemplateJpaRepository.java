package com.destiny.couponservice.infrastructure.repository;

import com.destiny.couponservice.domain.entity.CouponTemplate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CouponTemplateJpaRepository extends JpaRepository<CouponTemplate, UUID>,
    JpaSpecificationExecutor<CouponTemplate> {

    boolean existsByCode(String code);

    List<CouponTemplate> findByIdIn(Collection<UUID> ids);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update CouponTemplate t
               set t.issueLimit = t.issueLimit - 1
             where t.id = :templateId
               and t.issueLimit is not null
               and t.issueLimit > 0
        """)
    int decreaseIssueLimit(@Param("templateId") UUID templateId);
}

