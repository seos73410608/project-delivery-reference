package com.seos.pmis.evidence.specification;

import com.seos.pmis.evidence.entity.Evidence;
import com.seos.pmis.evidence.enums.EvidenceType;
import com.seos.pmis.evidence.enums.VerificationStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;

/**
 * Evidence Specification
 *
 * Evidence 검색 조건을 동적으로 조합한다.
 *
 * 지원 검색 조건:
 * - projectId
 * - requirementId
 * - wbsId
 * - keyword
 * - evidenceType
 * - verificationStatus
 *
 * Specification은 검색 조건 조합만 담당하며,
 * 페이징 및 정렬은 Service / SearchPageableFactory에서 담당한다.
 */
public final class EvidenceSpecification {

    private EvidenceSpecification() {
    }

    /**
     * 프로젝트 ID 검색
     */
    public static Specification<Evidence> projectId(
            Long projectId
    ) {
        return (root, query, criteriaBuilder) -> {

            if (projectId == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    root.get("project").get("id"),
                    projectId
            );
        };
    }

    /**
     * Evidence Requirement ID 검색
     */
    public static Specification<Evidence> requirementId(
            Long requirementId
    ) {
        return (root, query, criteriaBuilder) -> {

            if (requirementId == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    root.get("requirement").get("id"),
                    requirementId
            );
        };
    }

    /**
     * WBS ID 검색
     */
    public static Specification<Evidence> wbsId(
            Long wbsId
    ) {
        return (root, query, criteriaBuilder) -> {

            if (wbsId == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    root.get("wbs").get("id"),
                    wbsId
            );
        };
    }

    /**
     * Keyword 검색
     *
     * evidenceKey, title, description 중
     * 하나라도 Keyword를 포함하면 검색된다.
     */
    public static Specification<Evidence> keyword(
            String keyword
    ) {
        return (root, query, criteriaBuilder) -> {

            if (!StringUtils.hasText(keyword)) {
                return criteriaBuilder.conjunction();
            }

            String searchKeyword =
                    "%" + keyword.trim().toLowerCase() + "%";

            Predicate evidenceKeyPredicate =
                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("evidenceKey")
                            ),
                            searchKeyword
                    );

            Predicate titlePredicate =
                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("title")
                            ),
                            searchKeyword
                    );

            Predicate descriptionPredicate =
                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("description")
                            ),
                            searchKeyword
                    );

            return criteriaBuilder.or(
                    evidenceKeyPredicate,
                    titlePredicate,
                    descriptionPredicate
            );
        };
    }

    /**
     * Evidence 유형 검색
     */
    public static Specification<Evidence> evidenceType(
            EvidenceType evidenceType
    ) {
        return (root, query, criteriaBuilder) -> {

            if (evidenceType == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    root.get("evidenceType"),
                    evidenceType
            );
        };
    }

    /**
     * Verification 상태 검색
     */
    public static Specification<Evidence> verificationStatus(
            VerificationStatus verificationStatus
    ) {
        return (root, query, criteriaBuilder) -> {

            if (verificationStatus == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    root.get("verificationStatus"),
                    verificationStatus
            );
        };
    }
}