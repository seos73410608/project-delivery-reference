package com.seos.pmis.change.specification;

import com.seos.pmis.change.dto.request.ChangeSearchRequest;
import com.seos.pmis.change.entity.Change;
import com.seos.pmis.change.entity.ChangeImpactLevel;
import com.seos.pmis.change.entity.ChangePriority;
import com.seos.pmis.change.entity.ChangeStatus;
import com.seos.pmis.change.entity.ChangeType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;

public final class ChangeSpecification {

    private ChangeSpecification() {
    }

    /**
     * 전체 Change 검색
     */
    public static Specification<Change> search(
            ChangeSearchRequest request
    ) {
        return build(request, null);
    }

    /**
     * 특정 프로젝트의 Change 검색
     */
    public static Specification<Change> search(
            ChangeSearchRequest request,
            Long projectId
    ) {
        return build(request, projectId);
    }

    /**
     * 검색 조건 생성
     */
    private static Specification<Change> build(
            ChangeSearchRequest request,
            Long projectId
    ) {

        return (Root<Change> root,
                CriteriaQuery<?> query,
                CriteriaBuilder criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (request != null) {

                /*
                 * 프로젝트 조건
                 *
                 * projectId가 별도로 전달되면
                 * 해당 프로젝트 ID를 우선 적용한다.
                 */
                if (projectId != null) {

                    predicates.add(
                            criteriaBuilder.equal(
                                    root.get("projectId"),
                                    projectId
                            )
                    );

                } else if (request.getProjectId() != null) {

                    predicates.add(
                            criteriaBuilder.equal(
                                    root.get("projectId"),
                                    request.getProjectId()
                            )
                    );
                }

                /*
                 * 키워드 검색
                 *
                 * Change Key
                 * 제목
                 * 설명
                 */
                if (StringUtils.hasText(request.getKeyword())) {

                    String keyword =
                            "%" + request.getKeyword().trim().toLowerCase() + "%";

                    Predicate changeKeyPredicate =
                            criteriaBuilder.like(
                                    criteriaBuilder.lower(
                                            root.get("changeKey")
                                    ),
                                    keyword
                            );

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

                    predicates.add(
                            criteriaBuilder.or(
                                    changeKeyPredicate,
                                    titlePredicate,
                                    descriptionPredicate
                            )
                    );
                }

                /*
                 * 상태
                 */
                ChangeStatus status = request.getStatus();

                if (status != null) {

                    predicates.add(
                            criteriaBuilder.equal(
                                    root.get("status"),
                                    status
                            )
                    );
                }

                /*
                 * 우선순위
                 */
                ChangePriority priority = request.getPriority();

                if (priority != null) {

                    predicates.add(
                            criteriaBuilder.equal(
                                    root.get("priority"),
                                    priority
                            )
                    );
                }

                /*
                 * 변경 유형
                 */
                ChangeType changeType = request.getChangeType();

                if (changeType != null) {

                    predicates.add(
                            criteriaBuilder.equal(
                                    root.get("changeType"),
                                    changeType
                            )
                    );
                }

                /*
                 * 영향도
                 */
                ChangeImpactLevel impactLevel =
                        request.getImpactLevel();

                if (impactLevel != null) {

                    predicates.add(
                            criteriaBuilder.equal(
                                    root.get("impactLevel"),
                                    impactLevel
                            )
                    );
                }

                /*
                 * 요청자
                 */
                if (request.getRequesterId() != null) {

                    predicates.add(
                            criteriaBuilder.equal(
                                    root.get("requesterId"),
                                    request.getRequesterId()
                            )
                    );
                }

                /*
                 * 담당자
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