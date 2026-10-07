package com.seos.pmis.evidence.specification;

import com.seos.pmis.evidence.entity.EvidenceRequirement;
import com.seos.pmis.evidence.enums.RequirementStatus;
import com.seos.pmis.evidence.enums.RequirementType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

/**
 * Evidence Requirement Specification
 *
 * Evidence Requirement 검색 조건을 동적으로 조합한다.
 *
 * 지원 검색 조건:
 * - projectId
 * - wbsId
 * - keyword
 * - requirementType
 * - required
 * - status
 *
 * Specification은 검색 조건 조합만 담당하며,
 * 페이징 및 정렬은 Service / SearchPageableFactory에서 담당한다.
 */
public final class EvidenceRequirementSpecification {

    private EvidenceRequirementSpecification() {
    }

    /**
     * 프로젝트 ID 검색
     */
    public static Specification<EvidenceRequirement> projectId(
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
     * WBS ID 검색
     */
    public static Specification<EvidenceRequirement> wbsId(
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
     * requirementKey, title, description 중
     * 하나라도 Keyword를 포함하면 검색된다.
     */
    public static Specification<EvidenceRequirement> keyword(
            String keyword
    ) {
        return (root, query, criteriaBuilder) -> {

            if (!StringUtils.hasText(keyword)) {
                return criteriaBuilder.conjunction();
            }

            String searchKeyword =
                    "%" + keyword.trim().toLowerCase() + "%";

            Predicate requirementKeyPredicate =
                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("requirementKey")
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
                    requirementKeyPredicate,
                    titlePredicate,
                    descriptionPredicate
            );
        };
    }

    /**
     * Evidence Requirement 유형 검색
     */
    public static Specification<EvidenceRequirement> requirementType(
            RequirementType requirementType
    ) {
        return (root, query, criteriaBuilder) -> {

            if (requirementType == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    root.get("requirementType"),
                    requirementType
            );
        };
    }

    /**
     * 필수 증적 여부 검색
     */
    public static Specification<EvidenceRequirement> required(
            Boolean required
    ) {
        return (root, query, criteriaBuilder) -> {

            if (required == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    root.get("required"),
                    required
            );
        };
    }

    /**
     * Evidence Requirement 상태 검색
     */
    public static Specification<EvidenceRequirement> status(
            RequirementStatus status
    ) {
        return (root, query, criteriaBuilder) -> {

            if (status == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    root.get("status"),
                    status
            );
        };
    }
}