
package com.seos.pmis.requirement.service;

import com.seos.common.exception.BusinessException;
import com.seos.common.exception.code.CommonErrorCode;
import com.seos.pmis.requirement.dto.request.OperationalScopeSearchRequest;
import com.seos.pmis.requirement.dto.response.OperationalScopeResponse;
import com.seos.pmis.requirement.entity.OperationalScope;
import com.seos.pmis.requirement.exception.OperationalScopeErrorCode;
import com.seos.pmis.requirement.mapper.OperationalScopeMapper;
import com.seos.pmis.requirement.repository.OperationalScopeRepository;
import com.seos.pmis.requirement.specification.OperationalScopeSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OperationalScopeQueryService {

    private final OperationalScopeRepository operationalScopeRepository;
    private final OperationalScopeMapper operationalScopeMapper;

    /**
     * 허용된 정렬 컬럼
     */
    private static final Set<String> ALLOWED_SORT_PROPERTIES = Set.of(
            "scopeCode",
            "scopeName",
            "scopeType",
            "status",
            "effectiveFrom",
            "effectiveTo",
            "createdAt",
            "updatedAt"
    );

    /**
     * Operational Scope 검색
     */
    public Page<OperationalScopeResponse> search(
            OperationalScopeSearchRequest request,
            Pageable pageable
    ) {

        validateSort(pageable);

        Specification<OperationalScope> specification =
                Specification
                        .where(
                                OperationalScopeSpecification.scopeCodeContains(
                                        request.getScopeCode()
                                )
                        )
                        .and(
                                OperationalScopeSpecification.scopeNameContains(
                                        request.getScopeName()
                                )
                        )
                        .and(
                                OperationalScopeSpecification.hasScopeType(
                                        request.getScopeType()
                                )
                        )
                        .and(
                                OperationalScopeSpecification.hasStatus(
                                        request.getStatus()
                                )
                        )
                        .and(
                                OperationalScopeSpecification.hasParentScopeId(
                                        request.getParentScopeId()
                                )
                        )
                        .and(
                                OperationalScopeSpecification.hasOwnerId(
                                        request.getOwnerId()
                                )
                        )
                        .and(
                                OperationalScopeSpecification.ownerKeywordContains(
                                        request.getOwnerKeyword()
                                )
                        );

        return operationalScopeRepository.findAll(
                        specification,
                        pageable
                )
                .map(operationalScopeMapper::toResponse);
    }

    /**
     * Operational Scope 상세 조회
     */
    public OperationalScopeResponse getDetail(Long id) {

        OperationalScope operationalScope =
                operationalScopeRepository.findById(id)
                        .orElseThrow(() ->
                                new BusinessException(
                                        OperationalScopeErrorCode.OPERATIONAL_SCOPE_NOT_FOUND
                                )
                        );

        return operationalScopeMapper.toResponse(operationalScope);
    }

    /**
     * 허용된 정렬 컬럼 검증
     */
    private void validateSort(Pageable pageable) {

        for (Sort.Order order : pageable.getSort()) {

            if (!ALLOWED_SORT_PROPERTIES.contains(order.getProperty())) {

                throw new BusinessException(
                        CommonErrorCode.INVALID_REQUEST
                );
            }
        }
    }
}
