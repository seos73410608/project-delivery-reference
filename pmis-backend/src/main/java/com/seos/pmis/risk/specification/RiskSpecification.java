package com.seos.pmis.risk.specification;

import com.seos.pmis.risk.dto.request.RiskSearchRequest;
import com.seos.pmis.risk.entity.Risk;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class RiskSpecification {

    private RiskSpecification() {
    }

    /**
     * Global Risk 검색
     *
     * request.projectId가 존재하면 해당 프로젝트 조건도 적용한다.
     */
    public static Specification<Risk> search(
            RiskSearchRequest request
    ) {
        return build(request, null);
    }

    /**
     * Project-scoped Risk 검색
     *
     * URL의 projectId를 기준으로 프로젝트 범위를 강제한다.
     */
    public static Specification<Risk> search(
            RiskSearchRequest request,
            Long projectId
    ) {
        return build(request, projectId);
    }

    private static Specification<Risk> build(
            RiskSearchRequest request,
            Long projectId
    ) {
        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (request != null) {

                /*
                 * Project-scoped 검색에서는
                 * URL의 projectId를 우선 적용한다.
                 */
                if (projectId != null) {

                    predicates.add(
                            criteriaBuilder.equal(
                                    root.get("projectId"),
                                    projectId
                            )
                    );

                } else if (request.getProjectId() != null) {

                    /*
                     * Global 검색에서는
                     * request의 projectId를 검색 조건으로 사용한다.
                     */
                    predicates.add(
                            criteriaBuilder.equal(
                                    root.get("projectId"),
                                    request.getProjectId()
                            )
                    );
                }

                /*
                 * Keyword 검색
                 *
                 * title / description / riskKey
                 */
                if (StringUtils.hasText(request.getKeyword())) {

                    String keyword =
                            "%" +
                            request.getKeyword()
                                    .trim()
                                    .toLowerCase() +
                            "%";

                    Predicate titlePredicate =
                            criteriaBuilder.like(
                                    criteriaBuilder.lower(
                                            root.get("title")
                                    ),
                                    keyword
                            );

                    Predicate descriptionPredicate =
                            criteriaBuilder.like(
                                    criteriaBuilder.lower(
                                            root.get("description")
                                    ),
                                    keyword
                            );

                    Predicate riskKeyPredicate =
                            criteriaBuilder.like(
                                    criteriaBuilder.lower(
                                            root.get("riskKey")
                                    ),
                                    keyword
                            );

                    predicates.add(
                            criteriaBuilder.or(
                                    titlePredicate,
                                    descriptionPredicate,
                                    riskKeyPredicate
                            )
                    );
                }

                /*
                 * Status
                 */
                if (request.getStatus() != null) {

                    predicates.add(
                            criteriaBuilder.equal(
                                    root.get("status"),
                                    request.getStatus()
                            )
                    );
                }

                /*
                 * Priority
                 */
                if (request.getPriority() != null) {

                    predicates.add(
                            criteriaBuilder.equal(
                                    root.get("priority"),
                                    request.getPriority()
                            )
                    );
                }

                /*
                 * Probability
                 */
                if (request.getProbability() != null) {

                    predicates.add(
                            criteriaBuilder.equal(
                                    root.get("probability"),
                                    request.getProbability()
                            )
                    );
                }

                /*
                 * Impact
                 */
                if (request.getImpact() != null) {

                    predicates.add(
                            criteriaBuilder.equal(
                                    root.get("impact"),
                                    request.getImpact()
                            )
                    );
                }

                /*
                 * Assignee
                 */
                if (request.getAssigneeId() != null) {

                    predicates.add(
                            criteriaBuilder.equal(
                                    root.get("assigneeId"),
                                    request.getAssigneeId()
                            )
                    );
                }
            }

            return criteriaBuilder.and(
                    predicates.toArray(new Predicate[0])
            );
        };
    }
}
