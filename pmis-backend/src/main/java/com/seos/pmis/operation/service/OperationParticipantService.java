package com.seos.pmis.operation.service;

import com.seos.common.exception.BusinessException;
import com.seos.pmis.operation.dto.response.OperationParticipantResponse;
import com.seos.pmis.operation.entity.OperationParticipant;
import com.seos.pmis.operation.exception.OperationErrorCode;
import com.seos.pmis.operation.mapper.OperationParticipantMapper;
import com.seos.pmis.operation.repository.OperationParticipantRepository;
import com.seos.pmis.operation.repository.OperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OperationParticipantService {

    private final OperationRepository operationRepository;
    private final OperationParticipantRepository participantRepository;
    private final OperationParticipantMapper participantMapper;

    /**
     * 인계 대상 참여자 목록 조회
     */
    public List<OperationParticipantResponse> findHandoverEligibleParticipants(
            Long operationId,
            LocalDate plannedHandoverDate) {

        validateOperation(operationId);

        List<OperationParticipant> participants =
                plannedHandoverDate == null
                        ? participantRepository
                                .findHandoverEligibleParticipants(operationId)
                        : participantRepository
                                .findHandoverEligibleParticipantsOnDate(
                                        operationId,
                                        plannedHandoverDate);

        return participants.stream()
                .map(participantMapper::toResponse)
                .toList();
    }

    /**
     * 운영 서비스 존재 여부 검증
     */
    private void validateOperation(Long operationId) {
        if (operationId == null
                || !operationRepository.existsById(operationId)) {
            throw new BusinessException(
                    OperationErrorCode.OPERATION_NOT_FOUND);
        }
    }
}