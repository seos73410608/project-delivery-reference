package com.seos.pmis.wbs.specification;

import com.seos.pmis.wbs.dto.request.WbsSearchRequest;
import com.seos.pmis.wbs.entity.Wbs;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * WBS 검색 Specification
 *
 * WBS 검색 요청을 JPA Specification으로 변환한다.
 *
 * 검색 조건:
 * - projectId
 * - keyword
 * - status
 * - parentId
 *
 * keyword 검색:
 * - wbsCode
 * - wbsName
 *
 * 정렬 및 페이징은 Specification에서 처리하지 않고
 * Service Layer에서 Pageable을 통해 처리한다.
 */
public final class WbsSpecification {

    private WbsSpecification() {
    }

    /**
     * WBS 검색 조건 생성
     *
     * @param request WBS 검색 요청
     * @return WBS 검색 Specification
     */
    public static Specification<Wbs> search(
            WbsSearchRequest request
    ) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            /*
             * 검색 요청이 없는 경우
             *
             * 별도의 검색 조건 없이
             * 전체 WBS를 조회한다.
             */
            if (request == null) {
                return criteriaBuilder.conjunction();
            }

            /*
             * 프로젝트 조건
             *
             * WBS는 특정 프로젝트에 소속되므로
             * projectId가 전달된 경우
             * 해당 프로젝트의 WBS만 조회한다.
             */
            if (request.getProjectId() != null) {

                predicates.add(
                        criteriaBuilder.equal(
                                root.get("project").get("id"),
                                request.getProjectId()
                        )
                );
            }

            /*
             * 상위 WBS 조건
             *
             * parentId가 전달된 경우
             * 특정 부모 WBS의 직계 하위 WBS만 조회한다.
             */
            if (request.getParentId() != null) {

                predicates.add(
                        criteriaBuilder.equal(
                                root.get("parent").get("id"),
                                request.getParentId()
                        )
                );
            }

            /*
             * 검색어 조건
             *
             * WBS Code 또는 WBS Name을 대상으로
             * 대소문자를 구분하지 않는 부분 일치 검색을 수행한다.
             */
            if (StringUtils.hasText(request.getKeyword())) {

                String keyword =
                        "%" + request.getKeyword().trim().toLowerCase() + "%";

                Predicate wbsCodePredicate =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("wbsCode")
                                ),
                                keyword
                        );

                Predicate wbsNamePredicate =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("wbsName")
                                ),
                                keyword
                        );

                /*
                 * WBS Code 또는 WBS Name 중 하나라도
                 * 검색어를 포함하면 검색 결과에 포함한다.
                 */
                predicates.add(
                        criteriaBuilder.or(
                                wbsCodePredicate,
                                wbsNamePredicate
                        )
                );
            }

            /*
             * WBS 상태 조건
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
             * 검색 조건이 없는 경우
             */
            if (predicates.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            /*
             * 모든 검색 조건은 AND로 결합한다.
             *
             * 예:
             *
             * projectId = 1
             * AND status = IN_PROGRESS
             * AND (wbsCode LIKE '%server%'
             *      OR wbsName LIKE '%server%')
             */
            return criteriaBuilder.and(
                    predicates.toArray(new Predicate[0])
            );
        };
    }
}
