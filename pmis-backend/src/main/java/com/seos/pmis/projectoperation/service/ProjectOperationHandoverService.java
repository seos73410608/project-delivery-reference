
package com.seos.pmis.projectoperation.service;

import com.seos.common.exception.BusinessException;
import com.seos.pmis.operation.entity.OperationParticipant;
import com.seos.pmis.operation.entity.OperationParticipantRole;
import com.seos.pmis.operation.repository.OperationParticipantRepository;
import com.seos.pmis.projectoperation.entity.ProjectOperation;
import com.seos.pmis.projectoperation.entity.ProjectOperationHandover;
import com.seos.pmis.projectoperation.entity.ProjectOperationHandoverStatus;
import com.seos.pmis.projectoperation.entity.ProjectOperationHandoverType;
import com.seos.pmis.projectoperation.entity.ProjectOperationStatus;
import com.seos.pmis.projectoperation.exception.ProjectOperationErrorCode;
import com.seos.pmis.projectoperation.repository.ProjectOperationHandoverRepository;
import com.seos.pmis.projectoperation.repository.ProjectOperationRepository;
import com.seos.pmis.user.entity.User;
import com.seos.pmis.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectOperationHandoverService {

    private static final List<ProjectOperationHandoverStatus> ACTIVE_STATUSES =
            List.of(
                    ProjectOperationHandoverStatus.REQUESTED,
                    ProjectOperationHandoverStatus.IN_PROGRESS,
                    ProjectOperationHandoverStatus.REVIEW,
                    ProjectOperationHandoverStatus.ACCEPTED
            );

    private final ProjectOperationHandoverRepository handoverRepository;
    private final ProjectOperationRepository projectOperationRepository;
    private final OperationParticipantRepository participantRepository;
    private final UserRepository userRepository;

    /**
     * 최초 운영 인수인계 생성
     */
    @Transactional
    public ProjectOperationHandover createInitial(
            Long projectOperationId,
            Long toParticipantId,
            LocalDate plannedHandoverDate,
            String notes) {

        ProjectOperation projectOperation =
                findProjectOperation(projectOperationId);

        validateProjectOperationStatus(projectOperation);
        validateNoActiveHandover(projectOperationId);

        List<ProjectOperationHandover> history =
                handoverRepository
                        .findAllByProjectOperation_IdOrderBySequenceNoAsc(
                                projectOperationId);

        if (!history.isEmpty()) {
            throw new BusinessException(
                    ProjectOperationErrorCode.HANDOVER_PREVIOUS_INVALID);
        }

        OperationParticipant toParticipant = findParticipant(
                toParticipantId,
                projectOperation,
                plannedHandoverDate);

        return handoverRepository.save(
                ProjectOperationHandover.builder()
                        .projectOperation(projectOperation)
                        .operation(projectOperation.getOperation())
                        .handoverType(ProjectOperationHandoverType.INITIAL)
                        .status(ProjectOperationHandoverStatus.DRAFT)
                        .sequenceNo(1)
                        .previousHandover(null)
                        .fromParticipant(null)
                        .toParticipant(toParticipant)
                        .plannedHandoverDate(plannedHandoverDate)
                        .notes(notes)
                        .build()
        );
    }

    /**
     * 운영자 변경 인수인계 생성
     *
     * 이전 인수인계의 인수자가 새로운 인계자가 된다.
     */
    @Transactional
    public ProjectOperationHandover createOperatorChange(
            Long projectOperationId,
            Long fromParticipantId,
            Long toParticipantId,
            LocalDate plannedHandoverDate,
            String notes) {

        ProjectOperation projectOperation =
                findProjectOperation(projectOperationId);

        validateProjectOperationStatus(projectOperation);
        validateNoActiveHandover(projectOperationId);

        ProjectOperationHandover previous =
                findCompletedPreviousHandover(projectOperationId);

        OperationParticipant fromParticipant = findParticipant(
                fromParticipantId,
                projectOperation,
                plannedHandoverDate);

        OperationParticipant toParticipant = findParticipant(
                toParticipantId,
                projectOperation,
                plannedHandoverDate);

        if (!fromParticipant.getId().equals(
                previous.getToParticipant().getId())) {
            throw new BusinessException(
                    ProjectOperationErrorCode.HANDOVER_PREVIOUS_INVALID);
        }

        if (fromParticipant.getId().equals(toParticipant.getId())) {
            throw new BusinessException(
                    ProjectOperationErrorCode.HANDOVER_PARTICIPANT_INVALID);
        }

        return createNextHandover(
                projectOperation,
                previous,
                ProjectOperationHandoverType.OPERATOR_CHANGE,
                fromParticipant,
                toParticipant,
                plannedHandoverDate,
                notes);
    }

    /**
     * 동일 운영자에게 재인수인계 생성
     *
     * 최초 재인수인계에서는 인계자를 지정한다.
     * 이후 재인수인계에서는 이전 인계자와 인수자를 유지한다.
     */
    @Transactional
    public ProjectOperationHandover createReHandover(
            Long projectOperationId,
            Long fromParticipantId,
            Long toParticipantId,
            LocalDate plannedHandoverDate,
            String notes) {

        ProjectOperation projectOperation =
                findProjectOperation(projectOperationId);

        validateProjectOperationStatus(projectOperation);
        validateNoActiveHandover(projectOperationId);

        ProjectOperationHandover previous =
                findCompletedPreviousHandover(projectOperationId);

        OperationParticipant fromParticipant = findParticipant(
                fromParticipantId,
                projectOperation,
                plannedHandoverDate);

        OperationParticipant toParticipant = findParticipant(
                toParticipantId,
                projectOperation,
                plannedHandoverDate);

        if (!toParticipant.getId().equals(
                previous.getToParticipant().getId())) {
            throw new BusinessException(
                    ProjectOperationErrorCode.HANDOVER_PREVIOUS_INVALID);
        }

        if (previous.getHandoverType()
                == ProjectOperationHandoverType.INITIAL) {

            if (fromParticipant.getId().equals(toParticipant.getId())) {
                throw new BusinessException(
                        ProjectOperationErrorCode.HANDOVER_PARTICIPANT_INVALID);
            }

        } else {
            if (previous.getFromParticipant() == null
                    || !fromParticipant.getId().equals(
                            previous.getFromParticipant().getId())) {
                throw new BusinessException(
                        ProjectOperationErrorCode.HANDOVER_PREVIOUS_INVALID);
            }
        }

        return createNextHandover(
                projectOperation,
                previous,
                ProjectOperationHandoverType.RE_HANDOVER,
                fromParticipant,
                toParticipant,
                plannedHandoverDate,
                notes);
    }

    /**
     * 인수인계 요청
     */
    @Transactional
    public ProjectOperationHandover request(
            Long handoverId,
            Long handedOverByUserId,
            LocalDateTime requestedAt) {

        ProjectOperationHandover handover = findHandover(handoverId);
        User handedOverBy = findEnabledUser(handedOverByUserId);

        if (requestedAt == null
                || (handover.getCreatedAt() != null
                    && requestedAt.isBefore(handover.getCreatedAt()))) {
            throw new BusinessException(
                    ProjectOperationErrorCode.HANDOVER_DATE_INVALID);
        }

        try {
            handover.request(handedOverBy, requestedAt);
        } catch (IllegalStateException e) {
            throw new BusinessException(
                    ProjectOperationErrorCode.HANDOVER_STATUS_INVALID);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(
                    ProjectOperationErrorCode.HANDOVER_DATE_INVALID);
        }

        return handover;
    }

    /**
     * 인수인계 작업 시작
     */
    @Transactional
    public ProjectOperationHandover start(Long handoverId) {
        ProjectOperationHandover handover = findHandover(handoverId);

        try {
            handover.start();
        } catch (IllegalStateException e) {
            throw new BusinessException(
                    ProjectOperationErrorCode.HANDOVER_STATUS_INVALID);
        }

        return handover;
    }

    /**
     * 인수인계 검토 요청
     */
    @Transactional
    public ProjectOperationHandover submitForReview(Long handoverId) {
        ProjectOperationHandover handover = findHandover(handoverId);

        try {
            handover.submitForReview();
        } catch (IllegalStateException e) {
            throw new BusinessException(
                    ProjectOperationErrorCode.HANDOVER_STATUS_INVALID);
        }

        return handover;
    }

    /**
     * 인수인계 승인
     */
    @Transactional
    public ProjectOperationHandover accept(
            Long handoverId,
            Long acceptedByUserId,
            LocalDateTime acceptedAt) {

        ProjectOperationHandover handover = findHandover(handoverId);
        User acceptedBy = findEnabledUser(acceptedByUserId);

        if (acceptedAt == null
                || handover.getRequestedAt() == null
                || acceptedAt.isBefore(handover.getRequestedAt())) {
            throw new BusinessException(
                    ProjectOperationErrorCode.HANDOVER_DATE_INVALID);
        }

        try {
            handover.accept(acceptedBy, acceptedAt);
        } catch (IllegalStateException e) {
            throw new BusinessException(
                    ProjectOperationErrorCode.HANDOVER_STATUS_INVALID);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(
                    ProjectOperationErrorCode.HANDOVER_DATE_INVALID);
        }

        return handover;
    }

    /**
     * 인수인계 완료
     */
    @Transactional
    public ProjectOperationHandover complete(
            Long handoverId,
            LocalDateTime completedAt) {

        ProjectOperationHandover handover = findHandover(handoverId);

        if (completedAt == null
                || handover.getAcceptedAt() == null
                || completedAt.isBefore(handover.getAcceptedAt())) {
            throw new BusinessException(
                    ProjectOperationErrorCode.HANDOVER_DATE_INVALID);
        }

        try {
            handover.complete(completedAt);
        } catch (IllegalStateException e) {
            throw new BusinessException(
                    ProjectOperationErrorCode.HANDOVER_STATUS_INVALID);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(
                    ProjectOperationErrorCode.HANDOVER_DATE_INVALID);
        }

        return handover;
    }

    /**
     * 인수인계 반려
     */
    @Transactional
    public ProjectOperationHandover reject(Long handoverId) {
        ProjectOperationHandover handover = findHandover(handoverId);

        try {
            handover.reject();
        } catch (IllegalStateException e) {
            throw new BusinessException(
                    ProjectOperationErrorCode.HANDOVER_STATUS_INVALID);
        }

        return handover;
    }

    /**
     * 인수인계 취소
     */
    @Transactional
    public ProjectOperationHandover cancel(Long handoverId) {
        ProjectOperationHandover handover = findHandover(handoverId);

        try {
            handover.cancel();
        } catch (IllegalStateException e) {
            throw new BusinessException(
                    ProjectOperationErrorCode.HANDOVER_STATUS_INVALID);
        }

        return handover;
    }

    /**
     * 프로젝트 운영 관계의 전체 인수인계 이력 조회
     */
    public List<ProjectOperationHandover> getHistory(
            Long projectOperationId) {

        findProjectOperation(projectOperationId);

        return handoverRepository
                .findAllByProjectOperation_IdOrderBySequenceNoAsc(
                        projectOperationId);
    }

    /**
     * 다음 인수인계 이력 생성
     */
    private ProjectOperationHandover createNextHandover(
            ProjectOperation projectOperation,
            ProjectOperationHandover previous,
            ProjectOperationHandoverType handoverType,
            OperationParticipant fromParticipant,
            OperationParticipant toParticipant,
            LocalDate plannedHandoverDate,
            String notes) {

        int nextSequence = previous.getSequenceNo() + 1;

        return handoverRepository.save(
                ProjectOperationHandover.builder()
                        .projectOperation(projectOperation)
                        .operation(projectOperation.getOperation())
                        .handoverType(handoverType)
                        .status(ProjectOperationHandoverStatus.DRAFT)
                        .sequenceNo(nextSequence)
                        .previousHandover(previous)
                        .fromParticipant(fromParticipant)
                        .toParticipant(toParticipant)
                        .plannedHandoverDate(plannedHandoverDate)
                        .notes(notes)
                        .build()
        );
    }

    /**
     * 프로젝트 운영 관계 조회
     */
    private ProjectOperation findProjectOperation(Long projectOperationId) {
        if (projectOperationId == null) {
            throw new BusinessException(
                    ProjectOperationErrorCode.PROJECT_OPERATION_NOT_FOUND);
        }

        return projectOperationRepository.findById(projectOperationId)
                .orElseThrow(() -> new BusinessException(
                        ProjectOperationErrorCode.PROJECT_OPERATION_NOT_FOUND));
    }

    /**
     * 인수인계 조회
     */
    private ProjectOperationHandover findHandover(Long handoverId) {
        if (handoverId == null) {
            throw new BusinessException(
                    ProjectOperationErrorCode.HANDOVER_NOT_FOUND);
        }

        return handoverRepository.findById(handoverId)
                .orElseThrow(() -> new BusinessException(
                        ProjectOperationErrorCode.HANDOVER_NOT_FOUND));
    }

    /**
     * 운영 참여자 조회 및 유효성 검증
     */
    private OperationParticipant findParticipant(
            Long participantId,
            ProjectOperation projectOperation,
            LocalDate plannedHandoverDate) {

        if (participantId == null) {
            throw new BusinessException(
                    ProjectOperationErrorCode.HANDOVER_PARTICIPANT_NOT_FOUND);
        }

        OperationParticipant participant =
                participantRepository.findById(participantId)
                        .orElseThrow(() -> new BusinessException(
                                ProjectOperationErrorCode.HANDOVER_PARTICIPANT_NOT_FOUND));

        if (!participant.getOperation().getId().equals(
                projectOperation.getOperation().getId())) {
            throw new BusinessException(
                    ProjectOperationErrorCode.HANDOVER_PARTICIPANT_INVALID);
        }

        validateParticipantRole(participant);
        validateParticipantPeriod(participant, plannedHandoverDate);

        return participant;
    }

    /**
     * 인수인계 참여자 역할 검증
     */
    private void validateParticipantRole(OperationParticipant participant) {
        OperationParticipantRole role = participant.getParticipantRole();

        if (role != OperationParticipantRole.OPERATOR
                && role != OperationParticipantRole.SERVICE_PROVIDER) {
            throw new BusinessException(
                    ProjectOperationErrorCode.HANDOVER_PARTICIPANT_INVALID);
        }
    }

    /**
     * 예정 인수인계일 기준 참여 기간 검증
     */
    private void validateParticipantPeriod(
            OperationParticipant participant,
            LocalDate plannedHandoverDate) {

        if (plannedHandoverDate == null) {
            return;
        }

        if (participant.getEffectiveFrom() != null
                && plannedHandoverDate.isBefore(
                        participant.getEffectiveFrom())) {
            throw new BusinessException(
                    ProjectOperationErrorCode.HANDOVER_PARTICIPANT_INVALID);
        }

        if (participant.getEffectiveTo() != null
                && plannedHandoverDate.isAfter(
                        participant.getEffectiveTo())) {
            throw new BusinessException(
                    ProjectOperationErrorCode.HANDOVER_PARTICIPANT_INVALID);
        }
    }

    /**
     * 활성 프로젝트 운영 관계인지 검증
     */
    private void validateProjectOperationStatus(
            ProjectOperation projectOperation) {

        if (projectOperation.getStatus() != ProjectOperationStatus.PLANNED
                && projectOperation.getStatus() != ProjectOperationStatus.ACTIVE) {
            throw new BusinessException(
                    ProjectOperationErrorCode.PROJECT_OPERATION_STATUS_INVALID);
        }
    }

    /**
     * 진행 중인 인수인계 존재 여부 검증
     */
    private void validateNoActiveHandover(Long projectOperationId) {
        if (handoverRepository.existsByProjectOperation_IdAndStatusIn(
                projectOperationId,
                ACTIVE_STATUSES)) {
            throw new BusinessException(
                    ProjectOperationErrorCode.HANDOVER_ALREADY_ACTIVE);
        }
    }

    /**
     * 가장 최근 인수인계가 완료되었는지 검증
     */
    private ProjectOperationHandover findCompletedPreviousHandover(
            Long projectOperationId) {

        ProjectOperationHandover previous =
                handoverRepository
                        .findFirstByProjectOperation_IdOrderBySequenceNoDesc(
                                projectOperationId)
                        .orElseThrow(() -> new BusinessException(
                                ProjectOperationErrorCode.HANDOVER_PREVIOUS_INVALID));

        if (previous.getStatus()
                != ProjectOperationHandoverStatus.COMPLETED) {
            throw new BusinessException(
                    ProjectOperationErrorCode.HANDOVER_PREVIOUS_INVALID);
        }

        return previous;
    }

    /**
     * 활성 사용자 조회
     */
    private User findEnabledUser(Long userId) {
        if (userId == null) {
            throw new BusinessException(
                    ProjectOperationErrorCode.HANDOVER_USER_NOT_FOUND);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        ProjectOperationErrorCode.HANDOVER_USER_NOT_FOUND));

        if (!Boolean.TRUE.equals(user.getEnabled())) {
            throw new BusinessException(
                    ProjectOperationErrorCode.HANDOVER_USER_DISABLED);
        }

        return user;
    }
}
