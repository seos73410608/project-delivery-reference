package com.seos.pmis.operation.service;

import com.seos.common.exception.BusinessException;
import com.seos.pmis.operation.dto.response.OperationResponse;
import com.seos.pmis.operation.entity.Operation;
import com.seos.pmis.operation.entity.OperationStatus;
import com.seos.pmis.operation.entity.OperationType;
import com.seos.pmis.operation.exception.OperationErrorCode;
import com.seos.pmis.operation.mapper.OperationMapper;
import com.seos.pmis.operation.repository.OperationRepository;
import com.seos.pmis.user.entity.User;
import com.seos.pmis.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OperationService {

    private final OperationRepository operationRepository;
    private final UserRepository userRepository;
    private final OperationMapper operationMapper;

    /**
     * 운영 서비스 생성
     */
    @Transactional
    public Operation create(
            String operationCode,
            String operationName,
            OperationType operationType,
            Long managerUserId,
            String description,
            LocalDate effectiveFrom,
            LocalDate effectiveTo) {
        validatePeriod(effectiveFrom, effectiveTo);

        if (operationRepository.existsByOperationCode(operationCode)) {
            throw new BusinessException(
                    OperationErrorCode.OPERATION_DUPLICATE);
        }

        User manager = findManager(managerUserId);

        Operation operation = Operation.builder()
                .operationCode(operationCode)
                .operationName(operationName)
                .operationType(operationType)
                .manager(manager)
                .description(description)
                .effectiveFrom(effectiveFrom)
                .effectiveTo(effectiveTo)
                .build();

        return operationRepository.save(operation);
    }

    /**
     * 운영 서비스 정보 수정
     */
    @Transactional
    public Operation update(
            Long operationId,
            String operationName,
            OperationType operationType,
            Long managerUserId,
            String description,
            LocalDate effectiveFrom,
            LocalDate effectiveTo) {
        Operation operation = findOperation(operationId);

        validatePeriod(effectiveFrom, effectiveTo);

        User manager = findManager(managerUserId);

        operation.update(
                operationName,
                operationType,
                manager,
                description,
                effectiveFrom,
                effectiveTo);

        return operation;
    }

    /**
     * 운영 서비스 상태 변경
     */
    @Transactional
    public Operation changeStatus(
            Long operationId,
            OperationStatus status) {
        Operation operation = findOperation(operationId);

        if (status == null) {
            throw new BusinessException(
                    OperationErrorCode.OPERATION_STATUS_INVALID);
        }

        operation.changeStatus(status);

        return operation;
    }

    /**
     * 운영 서비스 단건 조회
     */
    public Operation getDetail(Long operationId) {
        return findOperation(operationId);
    }

    /**
     * 운영 코드로 운영 서비스 조회
     */
    public Operation findByCode(String operationCode) {
        if (operationCode == null || operationCode.isBlank()) {
            throw new BusinessException(
                    OperationErrorCode.OPERATION_NOT_FOUND);
        }

        return operationRepository.findByOperationCode(operationCode)
                .orElseThrow(() -> new BusinessException(
                        OperationErrorCode.OPERATION_NOT_FOUND));
    }

    /**
     * 전체 운영 서비스 조회
     */
    public List<Operation> findAll() {
        return operationRepository.findAll();
    }

    /**
     * 운영 서비스 생성 및 응답 반환
     */
    @Transactional
    public OperationResponse createResponse(
            String operationCode,
            String operationName,
            OperationType operationType,
            Long managerUserId,
            String description,
            LocalDate effectiveFrom,
            LocalDate effectiveTo) {
        return operationMapper.toResponse(
                create(
                        operationCode,
                        operationName,
                        operationType,
                        managerUserId,
                        description,
                        effectiveFrom,
                        effectiveTo));
    }

    /**
     * 운영 서비스 수정 및 응답 반환
     */
    @Transactional
    public OperationResponse updateResponse(
            Long operationId,
            String operationName,
            OperationType operationType,
            Long managerUserId,
            String description,
            LocalDate effectiveFrom,
            LocalDate effectiveTo) {
        return operationMapper.toResponse(
                update(
                        operationId,
                        operationName,
                        operationType,
                        managerUserId,
                        description,
                        effectiveFrom,
                        effectiveTo));
    }

    /**
     * 운영 서비스 상태 변경 및 응답 반환
     */
    @Transactional
    public OperationResponse changeStatusResponse(
            Long operationId,
            OperationStatus status) {
        return operationMapper.toResponse(
                changeStatus(operationId, status));
    }

    /**
     * 운영 서비스 단건 조회 및 응답 반환
     */
    public OperationResponse getDetailResponse(Long operationId) {
        return operationMapper.toResponse(
                getDetail(operationId));
    }

    /**
     * 운영 코드 조회 및 응답 반환
     */
    public OperationResponse findResponseByCode(String operationCode) {
        return operationMapper.toResponse(
                findByCode(operationCode));
    }

    /**
     * 전체 운영 서비스 목록 응답 반환
     */
    public List<OperationResponse> findAllResponses() {
        return findAll().stream()
                .map(operationMapper::toResponse)
                .toList();
    }

    /**
     * 운영 서비스 조회
     */
    private Operation findOperation(Long operationId) {
        if (operationId == null) {
            throw new BusinessException(
                    OperationErrorCode.OPERATION_NOT_FOUND);
        }

        return operationRepository.findById(operationId)
                .orElseThrow(() -> new BusinessException(
                        OperationErrorCode.OPERATION_NOT_FOUND));
    }

    /**
     * 운영 담당자 조회
     */
    private User findManager(Long managerUserId) {
        if (managerUserId == null) {
            return null;
        }

        return userRepository.findById(managerUserId)
                .orElseThrow(() -> new BusinessException(
                        OperationErrorCode.OPERATION_MANAGER_NOT_FOUND));
    }

    /**
     * 운영 기간 검증
     */
    private void validatePeriod(
            LocalDate effectiveFrom,
            LocalDate effectiveTo) {
        if (effectiveTo != null
                && (effectiveFrom == null
                        || effectiveFrom.isAfter(effectiveTo))) {
            throw new BusinessException(
                    OperationErrorCode.OPERATION_PERIOD_INVALID);
        }
    }
}