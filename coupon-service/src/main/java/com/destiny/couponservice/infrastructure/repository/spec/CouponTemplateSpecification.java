package com.destiny.couponservice.infrastructure.repository.spec;

import com.destiny.couponservice.domain.entity.CouponTemplate;
import com.destiny.couponservice.domain.enums.DiscountType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public final class CouponTemplateSpecification {

    private CouponTemplateSpecification() {
    }

    public static Specification<CouponTemplate> codeContains(String code) {
        if (!StringUtils.hasText(code)) {
            return null;
        }
        return (root, query, cb) ->
            cb.like(cb.lower(root.get("code")), "%" + code.toLowerCase() + "%");
    }

    public static Specification<CouponTemplate> nameContains(String name) {
        if (!StringUtils.hasText(name)) {
            return null;
        }
        return (root, query, cb) ->
            cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<CouponTemplate> typeEquals(DiscountType discountType) {
        if (discountType == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("discountType"), discountType);
    }
}
