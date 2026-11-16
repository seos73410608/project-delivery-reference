package com.seos.pmis.requirement.specification;

import com.seos.pmis.requirement.entity.OperationalScope;
import com.seos.pmis.requirement.entity.OperationalScopeStatus;
import com.seos.pmis.requirement.entity.OperationalScopeType;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

public final class OperationalScopeSpecification {

    private OperationalScopeSpecification() {
    }

    /**
     * Operational Scope 코드 부분 검색
     */
    public static Specification<OperationalScope> scopeCodeContains(String scopeCode) {

        return (root, query, cb) -> {

            if (scopeCode == null || scopeCode.isBlank()) {
                return null;
            }

            return cb.like(
                    cb.lower(root.get("scopeCode")),
                    "%" + scopeCode.trim().toLowerCase() + "%"
            );
        };
    }

    /**
     * Operational Scope 이름 부분 검색
     */
    public static Specification<OperationalScope> scopeNameContains(String scopeName) {

        return (root, query, cb) -> {

            if (scopeName == null || scopeName.isBlank()) {
                return null;
            }

            return cb.like(
                    cb.lower(root.get("scopeName")),
                    "%" + scopeName.trim().toLowerCase() + "%"
            );
        };
    }

    /**
     * Operational Scope 유형
     */
    public static Specification<OperationalScope> hasScopeType(OperationalScopeType scopeType) {

        return (root, query, cb) -> {

            if (scopeType == null) {
                return null;
            }

            return cb.equal(root.get("scopeType"), scopeType);
        };
    }

    /**
     * Operational Scope 상태
     */
    public static Specification<OperationalScope> hasStatus(OperationalScopeStatus status) {

        return (root, query, cb) -> {

            if (status == null) {
                return null;
            }

            return cb.equal(root.get("status"), status);
        };
    }

    /**
     * 상위 Operational Scope ID
     */
    public static Specification<OperationalScope> hasParentScopeId(Long parentScopeId) {

        return (root, query, cb) -> {

            if (parentScopeId == null) {
                return null;
            }

            return cb.equal(
                    root.get("parentScope").get("id"),
                    parentScopeId
            );
        };
    }

    /**
     * 담당자 사용자 ID
     */
    public static Specification<OperationalScope> hasOwnerId(Long ownerId) {

        return (root, query, cb) -> {

            if (ownerId == null) {
                return null;
            }

            return cb.equal(
                    root.get("owner").get("id"),
                    ownerId
            );
        };
    }

    /**
     * 담당자 로그인 ID 또는 이름 부분 검색
     */
    public static Specification<OperationalScope> ownerKeywordContains(String ownerKeyword) {

        return (root, query, cb) -> {

            if (ownerKeyword == null || ownerKeyword.isBlank()) {
                return null;
            }

            Join<OperationalScope, ?> owner =
                    root.join("owner", JoinType.LEFT);

            String keyword = "%" + ownerKeyword.trim().toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(owner.get("username")), keyword),
                    cb.like(cb.lower(owner.get("name")), keyword)
            );
        };
    }
}