
package com.seos.pmis.operationscope.service;

import com.seos.common.exception.BusinessException;
import com.seos.pmis.operation.entity.Operation;
import com.seos.pmis.operation.repository.OperationRepository;
import com.seos.pmis.operationscope.dto.response.OperationScopeResponse;
import com.seos.pmis.operationscope.entity.OperationScope;
import com.seos.pmis.operationscope.entity.OperationScopeStatus;
import com.seos.pmis.operationscope.exception.OperationScopeErrorCode;
import com.seos.pmis.operationscope.mapper.OperationScopeMapper;
import com.seos.pmis.operationscope.repository.OperationScopeRepository;
import com.seos.pmis.requirement.entity.OperationalScope;
import com.seos.pmis.requirement.repository.OperationalScopeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Operation-OperationalScope 관계 관리 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OperationScopeService {

    private final OperationScopeRepository operationScopeRepository;
    private final OperationRepository operationRepository;
    private final OperationalScopeRepository operationalScopeRepository;
    private final OperationScopeMapper operationScopeMapper;

    /**
     * 운영 서비스와 운영 범위 연결
     */
    @Transactional
    public OperationScope create(
            Long operationId,
            Long operationalScopeId,
            LocalDate effectiveFrom,
            LocalDate effectiveTo,
            String description) {
        validatePeriod(effectiveFrom, effectiveTo);

        Operation operation = findOperation(operationId);
        OperationalScope operationalScope = findOperationalScope(operationalScopeId);

        validateDuplicateRelationship(operationId, operationalScopeId);

        OperationScope operationScope = OperationScope.builder()
                .operation(operation)
                .operationalScope(operationalScope)
                .status(OperationScopeStatus.PLANNED)
                .effectiveFrom(effectiveFrom)
                .effectiveTo(effectiveTo)
                .description(description)
                .build();

        return operationScopeRepository.save(operationScope);
    }

    /**
     * 운영 범위 관계 정보 수정
     */
    @Transactional
    public OperationScope update(
            Long operationScopeId,
            LocalDate effectiveFrom,
            LocalDate effectiveTo,
            String description) {
        OperationScope operationScope = findOperationScope(operationScopeId);

        validatePeriod(effectiveFrom, effectiveTo);

        operationScope.update(
                effectiveFrom,
                effectiveTo,
                description);

        return operationScope;
    }

    /**
     * 운영 범위 관계 상태 변경
     */
    @Transactional
    public OperationScope changeStatus(
            Long operationScopeId,
            OperationScopeStatus status) {
        OperationScope operationScope = findOperationScope(operationScopeId);

        validateStatusTransition(operationScope.getStatus(), status);

        operationScope.changeStatus(status);

        return operationScope;
    }

    /**
     * 운영 범위 관계 단건 조회
     */
    public OperationScope getDetail(Long operationScopeId) {
        return findOperationScope(operationScopeId);
    }

    /**
     * 운영 서비스에 연결된 운영 범위 관계 조회
     */
    public List<OperationScope> findByOperation(Long operationId) {
        findOperation(operationId);

        return operationScopeRepository.findAllByOperation_Id(operationId);
    }

    /**
     * 운영 범위에 연결된 운영 서비스 관계 조회
     */
    public List<OperationScope> findByOperationalScope(
            Long operationalScopeId) {
        findOperationalScope(operationalScopeId);

        return operationScopeRepository
                .findAllByOperationalScope_Id(operationalScopeId);
    }

    /**
     * 운영 서비스와 운영 범위 연결 및 응답 반환
     */
    @Transactional
    public OperationScopeResponse createResponse(
            Long operationId,
            Long operationalScopeId,
            LocalDate effectiveFrom,
            LocalDate effectiveTo,
            String description) {
        return operationScopeMapper.toResponse(
                create(
                        operationId,
                        operationalScopeId,
                        effectiveFrom,
                        effectiveTo,
                        description));
    }

    /**
     * 운영 범위 관계 수정 및 응답 반환
     */
    @Transactional
    public OperationScopeResponse updateResponse(
            Long operationScopeId,
            LocalDate effectiveFrom,
            LocalDate effectiveTo,
            String description) {
        return operationScopeMapper.toResponse(
                update(
                        operationScopeId,
                        effectiveFrom,
                        effectiveTo,
                        description));
    }

    /**
     * 운영 범위 관계 상태 변경 및 응답 반환
     */
    @Transactional
    public OperationScopeResponse changeStatusResponse(
            Long operationScopeId,
            OperationScopeStatus status) {
        return operationScopeMapper.toResponse(
                changeStatus(operationScopeId, status));
    }

    /**
     * 운영 범위 관계 단건 조회 및 응답 반환
     */
    public OperationScopeResponse getDetailResponse(Long operationScopeId) {
        return operationScopeMapper.toResponse(
                getDetail(operationScopeId));
    }

    /**
     * 운영 서비스에 연결된 운영 범위 관계 목록 응답 반환
     */
    public List<OperationScopeResponse> findResponsesByOperation(
            Long operationId) {
        return findByOperation(operationId).stream()
                .map(operationScopeMapper::toResponse)
                .toList();
    }

    /**
     * 운영 범위에 연결된 운영 서비스 관계 목록 응답 반환
     */
    public List<OperationScopeResponse> findResponsesByOperationalScope(
            Long operationalScopeId) {
        return findByOperationalScope(operationalScopeId).stream()
                .map(operationScopeMapper::toResponse)
                .toList();
    }

    /**
     * 운영 서비스 조회
     */
    private Operation findOperation(Long operationId) {
        if (operationId == null) {
            throw new BusinessException(
                    OperationScopeErrorCode.OPERATION_NOT_FOUND);
        }

        return operationRepository.findById(operationId)
                .orElseThrow(() -> new BusinessException(
                        OperationScopeErrorCode.OPERATION_NOT_FOUND));
    }

    /**
     * 운영 범위 조회
     */
    private OperationalScope findOperationalScope(
            Long operationalScopeId) {
        if (operationalScopeId == null) {
            throw new BusinessException(
                    OperationScopeErrorCode.OPERATIONAL_SCOPE_NOT_FOUND);
        }

        return operationalScopeRepository.findById(operationalScopeId)
                .orElseThrow(() -> new BusinessException(
                        OperationScopeErrorCode.OPERATIONAL_SCOPE_NOT_FOUND));
    }

    /**
     * 운영 범위 관계 조회
     */
    private OperationScope findOperationScope(Long operationScopeId) {
        if (operationScopeId == null) {
            throw new BusinessException(
                    OperationScopeErrorCode.OPERATION_SCOPE_NOT_FOUND);
        }

        return operationScopeRepository.findById(operationScopeId)
                .orElseThrow(() -> new BusinessException(
                        OperationScopeErrorCode.OPERATION_SCOPE_NOT_FOUND));
    }

    /**
     * 활성 운영 서비스-운영 범위 관계 중복 검증
     */
    private void validateDuplicateRelationship(
            Long operationId,
            Long operationalScopeId) {
        boolean plannedExists = operationScopeRepository
                .existsByOperation_IdAndOperationalScope_IdAndStatus(
                        operationId,
                        operationalScopeId,
                        OperationScopeStatus.PLANNED);

        boolean activeExists = operationScopeRepository
                .existsByOperation_IdAndOperationalScope_IdAndStatus(
                        operationId,
                        operationalScopeId,
                        OperationScopeStatus.ACTIVE);

        if (plannedExists || activeExists) {
            throw new BusinessException(
                    OperationScopeErrorCode.OPERATION_SCOPE_DUPLICATE);
        }
    }

    /**
     * 관계 상태 전이 검증
     */
    private void validateStatusTransition(
            OperationScopeStatus currentStatus,
            OperationScopeStatus newStatus) {
        if (newStatus == null) {
            throw new BusinessException(
                    OperationScopeErrorCode.OPERATION_SCOPE_STATUS_INVALID);
        }

        boolean validTransition = switch (currentStatus) {
            case PLANNED ->
                newStatus == OperationScopeStatus.ACTIVE
                        || newStatus == OperationScopeStatus.CANCELLED;

            case ACTIVE ->
                newStatus == OperationScopeStatus.COMPLETED
                        || newStatus == OperationScopeStatus.CANCELLED;

            case COMPLETED, CANCELLED -> false;
        };

        if (!validTransition) {
            throw new BusinessException(
                    OperationScopeErrorCode.OPERATION_SCOPE_STATUS_INVALID);
        }
    }

    /**
     * 관계 기간 검증
     */
    private void validatePeriod(
            LocalDate effectiveFrom,
            LocalDate effectiveTo) {
        if (effectiveTo != null
                && (effectiveFrom == null
                        || effectiveFrom.isAfter(effectiveTo))) {
            throw new BusinessException(
                    OperationScopeErrorCode.OPERATION_SCOPE_PERIOD_INVALID);
        }
    }
}
