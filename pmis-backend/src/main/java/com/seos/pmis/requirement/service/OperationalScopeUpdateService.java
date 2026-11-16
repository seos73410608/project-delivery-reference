
package com.seos.pmis.requirement.service;

import com.seos.common.exception.BusinessException;
import com.seos.pmis.requirement.dto.request.OperationalScopeUpdateRequest;
import com.seos.pmis.requirement.dto.response.OperationalScopeResponse;
import com.seos.pmis.requirement.entity.OperationalScope;
import com.seos.pmis.requirement.exception.OperationalScopeErrorCode;
import com.seos.pmis.requirement.mapper.OperationalScopeMapper;
import com.seos.pmis.requirement.repository.OperationalScopeRepository;
import com.seos.pmis.user.entity.User;
import com.seos.pmis.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OperationalScopeUpdateService {

    private final OperationalScopeRepository operationalScopeRepository;
    private final UserRepository userRepository;
    private final OperationalScopeMapper operationalScopeMapper;

    @Transactional
    public OperationalScopeResponse update(
            Long id,
            OperationalScopeUpdateRequest request
    ) {
        OperationalScope operationalScope =
                operationalScopeRepository.findById(id)
                        .orElseThrow(() -> new BusinessException(
                                OperationalScopeErrorCode.OPERATIONAL_SCOPE_NOT_FOUND
                        ));

        validateVersion(
                request.getVersion(),
                operationalScope.getVersion()
        );

        validateEffectivePeriod(
                request.getEffectiveFrom(),
                request.getEffectiveTo()
        );

        OperationalScope parentScope =
                resolveParentScope(
                        id,
                        request.getParentScopeId()
                );

        User owner =
                resolveOwner(request.getOwnerId());

        operationalScope.update(
                request.getScopeName(),
                request.getScopeType(),
                parentScope,
                owner,
                request.getDescription(),
                request.getEffectiveFrom(),
                request.getEffectiveTo()
        );

        try {
            OperationalScope updatedScope =
                    operationalScopeRepository.saveAndFlush(operationalScope);

            return operationalScopeMapper.toResponse(updatedScope);

        } catch (OptimisticLockingFailureException exception) {
            throw new BusinessException(
                    OperationalScopeErrorCode.OPERATIONAL_SCOPE_VERSION_CONFLICT
            );
        }
    }

    private void validateVersion(
            Long requestVersion,
            Long currentVersion
    ) {
        if (!Objects.equals(requestVersion, currentVersion)) {
            throw new BusinessException(
                    OperationalScopeErrorCode.OPERATIONAL_SCOPE_VERSION_CONFLICT
            );
        }
    }

    private void validateEffectivePeriod(
            LocalDate effectiveFrom,
            LocalDate effectiveTo
    ) {
        if (effectiveTo != null
                && (effectiveFrom == null
                || effectiveFrom.isAfter(effectiveTo))) {
            throw new BusinessException(
                    OperationalScopeErrorCode.OPERATIONAL_SCOPE_EFFECTIVE_PERIOD_INVALID
            );
        }
    }

    private OperationalScope resolveParentScope(
            Long operationalScopeId,
            Long parentScopeId
    ) {
        if (parentScopeId == null) {
            return null;
        }

        OperationalScope parentScope =
                operationalScopeRepository.findById(parentScopeId)
                        .orElseThrow(() -> new BusinessException(
                                OperationalScopeErrorCode.OPERATIONAL_SCOPE_PARENT_NOT_FOUND
                        ));

        validateParentHierarchy(
                operationalScopeId,
                parentScope
        );

        return parentScope;
    }

    private void validateParentHierarchy(
            Long operationalScopeId,
            OperationalScope parentScope
    ) {
        Set<Long> visitedScopeIds = new HashSet<>();

        OperationalScope currentScope = parentScope;

        while (currentScope != null) {
            Long currentScopeId = currentScope.getId();

            if (Objects.equals(operationalScopeId, currentScopeId)) {
                throw new BusinessException(
                        OperationalScopeErrorCode.OPERATIONAL_SCOPE_PARENT_INVALID
                );
            }

            if (!visitedScopeIds.add(currentScopeId)) {
                throw new BusinessException(
                        OperationalScopeErrorCode.OPERATIONAL_SCOPE_PARENT_INVALID
                );
            }

            currentScope = currentScope.getParentScope();
        }
    }

    private User resolveOwner(Long ownerId) {
        if (ownerId == null) {
            return null;
        }

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new BusinessException(
                        OperationalScopeErrorCode.OPERATIONAL_SCOPE_OWNER_NOT_FOUND
                ));

        if (!Boolean.TRUE.equals(owner.getEnabled())) {
            throw new BusinessException(
                    OperationalScopeErrorCode.OPERATIONAL_SCOPE_OWNER_DISABLED
            );
        }

        return owner;
    }
}
