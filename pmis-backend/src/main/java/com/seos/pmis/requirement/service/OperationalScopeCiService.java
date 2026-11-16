
package com.seos.pmis.requirement.service;

import com.seos.cmdb.ci.entity.ConfigurationItem;
import com.seos.cmdb.ci.exception.ConfigurationItemErrorCode;
import com.seos.cmdb.ci.repository.ConfigurationItemRepository;
import com.seos.common.exception.BusinessException;
import com.seos.pmis.requirement.dto.response.OperationalScopeCiResponse;
import com.seos.pmis.requirement.entity.OperationalScope;
import com.seos.pmis.requirement.entity.OperationalScopeCi;
import com.seos.pmis.requirement.exception.OperationalScopeCiErrorCode;
import com.seos.pmis.requirement.exception.OperationalScopeErrorCode;
import com.seos.pmis.requirement.mapper.OperationalScopeCiMapper;
import com.seos.pmis.requirement.repository.OperationalScopeCiRepository;
import com.seos.pmis.requirement.repository.OperationalScopeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OperationalScopeCiService {

    private final OperationalScopeRepository operationalScopeRepository;
    private final OperationalScopeCiRepository operationalScopeCiRepository;
    private final ConfigurationItemRepository configurationItemRepository;
    private final OperationalScopeCiMapper operationalScopeCiMapper;

    /**
     * Operational Scope에 Configuration Item 연결
     */
    @Transactional
    public OperationalScopeCiResponse connect(Long scopeId, Long ciId) {

        OperationalScope operationalScope =
                findOperationalScope(scopeId);

        ConfigurationItem configurationItem =
                findConfigurationItem(ciId);

        if (operationalScopeCiRepository
                .existsByOperationalScopeIdAndConfigurationItemId(
                        scopeId,
                        ciId
                )) {
            throw new BusinessException(
                    OperationalScopeCiErrorCode.OPERATIONAL_SCOPE_CI_ALREADY_EXISTS
            );
        }

        OperationalScopeCi operationalScopeCi =
                OperationalScopeCi.create(
                        operationalScope,
                        configurationItem
                );

        OperationalScopeCi savedOperationalScopeCi =
                operationalScopeCiRepository.save(operationalScopeCi);

        return operationalScopeCiMapper.toResponse(
                savedOperationalScopeCi
        );
    }

    /**
     * Operational Scope에 연결된 Configuration Item 목록 조회
     */
    public List<OperationalScopeCiResponse> getAllByScopeId(
            Long scopeId
    ) {

        findOperationalScope(scopeId);

        return operationalScopeCiRepository
                .findAllByOperationalScopeId(scopeId)
                .stream()
                .map(operationalScopeCiMapper::toResponse)
                .toList();
    }

    /**
     * Operational Scope와 Configuration Item의 연결 상세 조회
     */
    public OperationalScopeCiResponse getByScopeIdAndCiId(
            Long scopeId,
            Long ciId
    ) {

        findOperationalScope(scopeId);

        OperationalScopeCi operationalScopeCi =
                findOperationalScopeCi(scopeId, ciId);

        return operationalScopeCiMapper.toResponse(
                operationalScopeCi
        );
    }

    /**
     * Operational Scope와 Configuration Item 연결 해제
     */
    @Transactional
    public void disconnect(Long scopeId, Long ciId) {

        findOperationalScope(scopeId);

        OperationalScopeCi operationalScopeCi =
                findOperationalScopeCi(scopeId, ciId);

        operationalScopeCiRepository.delete(operationalScopeCi);
    }

    /**
     * Operational Scope 조회
     */
    private OperationalScope findOperationalScope(Long scopeId) {

        return operationalScopeRepository.findById(scopeId)
                .orElseThrow(() ->
                        new BusinessException(
                                OperationalScopeErrorCode.OPERATIONAL_SCOPE_NOT_FOUND
                        )
                );
    }

    /**
     * Configuration Item 조회
     */
    private ConfigurationItem findConfigurationItem(Long ciId) {

        return configurationItemRepository.findById(ciId)
                .orElseThrow(() ->
                        new BusinessException(
                                ConfigurationItemErrorCode.CI_NOT_FOUND
                        )
                );
    }

    /**
     * Operational Scope-CI 연결 조회
     */
    private OperationalScopeCi findOperationalScopeCi(
            Long scopeId,
            Long ciId
    ) {

        return operationalScopeCiRepository
                .findByOperationalScopeIdAndConfigurationItemId(
                        scopeId,
                        ciId
                )
                .orElseThrow(() ->
                        new BusinessException(
                                OperationalScopeCiErrorCode.OPERATIONAL_SCOPE_CI_NOT_FOUND
                        )
                );
    }
}
