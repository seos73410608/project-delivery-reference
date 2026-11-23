
package com.seos.pmis.projectoperation.service;

import com.seos.common.exception.BusinessException;
import com.seos.pmis.operation.entity.Operation;
import com.seos.pmis.operation.entity.OperationParticipant;
import com.seos.pmis.operation.entity.OperationParticipantRole;
import com.seos.pmis.operation.entity.OperationType;
import com.seos.pmis.operation.repository.OperationParticipantRepository;
import com.seos.pmis.projectoperation.dto.response.ProjectOperationHandoverResponse;
import com.seos.pmis.projectoperation.entity.ProjectOperation;
import com.seos.pmis.projectoperation.entity.ProjectOperationHandover;
import com.seos.pmis.projectoperation.entity.ProjectOperationHandoverStatus;
import com.seos.pmis.projectoperation.entity.ProjectOperationHandoverType;
import com.seos.pmis.projectoperation.entity.ProjectOperationRelationshipType;
import com.seos.pmis.projectoperation.entity.ProjectOperationStatus;
import com.seos.pmis.projectoperation.exception.ProjectOperationErrorCode;
import com.seos.pmis.projectoperation.mapper.ProjectOperationHandoverMapper;
import com.seos.pmis.projectoperation.repository.ProjectOperationHandoverRepository;
import com.seos.pmis.projectoperation.repository.ProjectOperationRepository;
import com.seos.pmis.user.entity.User;
import com.seos.pmis.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectOperationHandoverServiceTest {

    @Mock
    private ProjectOperationHandoverRepository handoverRepository;

    @Mock
    private ProjectOperationRepository projectOperationRepository;

    @Mock
    private OperationParticipantRepository participantRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectOperationHandoverMapper handoverMapper;

    @InjectMocks
    private ProjectOperationHandoverService service;

    private Operation operation;
    private ProjectOperation projectOperation;
    private OperationParticipant participantA;
    private OperationParticipant participantB;
    private OperationParticipant participantC;
    private User user;
    private ProjectOperationHandover initialHandover;
    private ProjectOperationHandover completedHandover;

    private final LocalDate plannedDate = LocalDate.of(2026, 12, 1);
    private final LocalDateTime requestedAt =
            LocalDateTime.of(2026, 11, 1, 10, 0);

    @BeforeEach
    void setUp() {
        operation = Operation.builder()
                .id(1L)
                .operationCode("OP-001")
                .operationName("테스트 운영 서비스")
                .operationType(OperationType.INFRASTRUCTURE_SERVICE)
                .build();

        projectOperation = ProjectOperation.builder()
                .id(1L)
                .operation(operation)
                .relationshipType(ProjectOperationRelationshipType.BUILD_RESULT)
                .status(ProjectOperationStatus.PLANNED)
                .build();

        participantA = createParticipant(
                10L,
                operation,
                OperationParticipantRole.OPERATOR);

        participantB = createParticipant(
                20L,
                operation,
                OperationParticipantRole.OPERATOR);

        participantC = createParticipant(
                30L,
                operation,
                OperationParticipantRole.SERVICE_PROVIDER);

        user = User.builder()
                .id(100L)
                .enabled(true)
                .build();

        initialHandover = ProjectOperationHandover.builder()
                .id(1L)
                .projectOperation(projectOperation)
                .operation(operation)
                .handoverType(ProjectOperationHandoverType.INITIAL)
                .status(ProjectOperationHandoverStatus.COMPLETED)
                .sequenceNo(1)
                .toParticipant(participantB)
                .build();

        completedHandover = ProjectOperationHandover.builder()
                .id(2L)
                .projectOperation(projectOperation)
                .operation(operation)
                .handoverType(ProjectOperationHandoverType.OPERATOR_CHANGE)
                .status(ProjectOperationHandoverStatus.COMPLETED)
                .sequenceNo(2)
                .previousHandover(initialHandover)
                .fromParticipant(participantA)
                .toParticipant(participantB)
                .build();
    }

    private OperationParticipant createParticipant(
            Long id,
            Operation operation,
            OperationParticipantRole role) {

        return OperationParticipant.builder()
                .id(id)
                .operation(operation)
                .participantRole(role)
                .effectiveFrom(LocalDate.of(2026, 1, 1))
                .build();
    }

    private void mockProjectOperation() {
        when(projectOperationRepository.findById(1L))
                .thenReturn(Optional.of(projectOperation));
    }

    private void mockNoActiveHandover() {
        when(handoverRepository
                .existsByProjectOperation_IdAndStatusIn(any(), any()))
                .thenReturn(false);
    }

    private void mockParticipant(OperationParticipant participant) {
        when(participantRepository.findById(participant.getId()))
                .thenReturn(Optional.of(participant));
    }

    private void mockHandover(ProjectOperationHandover handover) {
        when(handoverRepository.findById(handover.getId()))
                .thenReturn(Optional.of(handover));
    }

    private ProjectOperationHandoverResponse createResponse(Long id) {
        return ProjectOperationHandoverResponse.builder()
                .id(id)
                .build();
    }

    @Test
    void createInitial_최초_인수인계를_생성한다() {
        mockProjectOperation();
        mockNoActiveHandover();

        when(handoverRepository
                .findAllByProjectOperation_IdOrderBySequenceNoAsc(1L))
                .thenReturn(List.of());

        mockParticipant(participantB);

        when(handoverRepository.save(any(ProjectOperationHandover.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProjectOperationHandover result = service.createInitial(
                1L, 20L, plannedDate, "최초 운영 인수인계");

        assertThat(result.getHandoverType())
                .isEqualTo(ProjectOperationHandoverType.INITIAL);
        assertThat(result.getStatus())
                .isEqualTo(ProjectOperationHandoverStatus.DRAFT);
        assertThat(result.getSequenceNo()).isEqualTo(1);
        assertThat(result.getFromParticipant()).isNull();
        assertThat(result.getToParticipant()).isEqualTo(participantB);
        assertThat(result.getPreviousHandover()).isNull();

        verify(handoverRepository).save(any(ProjectOperationHandover.class));
    }

    @Test
    void createInitial_기존_이력이_있으면_생성할_수_없다() {
        mockProjectOperation();
        mockNoActiveHandover();

        when(handoverRepository
                .findAllByProjectOperation_IdOrderBySequenceNoAsc(1L))
                .thenReturn(List.of(initialHandover));

        assertThatThrownBy(() ->
                service.createInitial(1L, 20L, plannedDate, null))
                .isInstanceOf(BusinessException.class);

        verify(handoverRepository, never())
                .save(any(ProjectOperationHandover.class));
    }

    @Test
    void createInitial_진행_중인_인수인계가_있으면_생성할_수_없다() {
        mockProjectOperation();

        when(handoverRepository
                .existsByProjectOperation_IdAndStatusIn(any(), any()))
                .thenReturn(true);

        assertThatThrownBy(() ->
                service.createInitial(1L, 20L, plannedDate, null))
                .isInstanceOf(BusinessException.class);

        verify(handoverRepository, never())
                .save(any(ProjectOperationHandover.class));
    }

    @Test
    void createInitial_종료된_프로젝트_운영_관계에서는_생성할_수_없다() {
        projectOperation.changeStatus(ProjectOperationStatus.COMPLETED);
        mockProjectOperation();

        assertThatThrownBy(() ->
                service.createInitial(1L, 20L, plannedDate, null))
                .isInstanceOf(BusinessException.class);

        verifyNoInteractions(participantRepository);
    }

    @Test
    void createInitial_존재하지_않는_참여자이면_예외가_발생한다() {
        mockProjectOperation();
        mockNoActiveHandover();

        when(handoverRepository
                .findAllByProjectOperation_IdOrderBySequenceNoAsc(1L))
                .thenReturn(List.of());

        when(participantRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.createInitial(1L, 999L, plannedDate, null))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void createOperatorChange_이전_인수자를_새로운_인계자로_연결한다() {
        mockProjectOperation();
        mockNoActiveHandover();

        when(handoverRepository
                .findFirstByProjectOperation_IdOrderBySequenceNoDesc(1L))
                .thenReturn(Optional.of(completedHandover));

        mockParticipant(participantB);
        mockParticipant(participantC);

        when(handoverRepository.save(any(ProjectOperationHandover.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProjectOperationHandover result = service.createOperatorChange(
                1L, 20L, 30L, plannedDate, "운영자 변경");

        assertThat(result.getHandoverType())
                .isEqualTo(ProjectOperationHandoverType.OPERATOR_CHANGE);
        assertThat(result.getSequenceNo()).isEqualTo(3);
        assertThat(result.getPreviousHandover()).isEqualTo(completedHandover);
        assertThat(result.getFromParticipant()).isEqualTo(participantB);
        assertThat(result.getToParticipant()).isEqualTo(participantC);
    }

    @Test
    void createOperatorChange_이전_인수자가_아닌_참여자는_인계자로_지정할_수_없다() {
        mockProjectOperation();
        mockNoActiveHandover();

        when(handoverRepository
                .findFirstByProjectOperation_IdOrderBySequenceNoDesc(1L))
                .thenReturn(Optional.of(completedHandover));

        mockParticipant(participantA);
        mockParticipant(participantC);

        assertThatThrownBy(() ->
                service.createOperatorChange(
                        1L, 10L, 30L, plannedDate, null))
                .isInstanceOf(BusinessException.class);

        verify(handoverRepository, never())
                .save(any(ProjectOperationHandover.class));
    }

    @Test
    void createOperatorChange_동일한_참여자를_인계자와_인수자로_지정할_수_없다() {
        mockProjectOperation();
        mockNoActiveHandover();

        when(handoverRepository
                .findFirstByProjectOperation_IdOrderBySequenceNoDesc(1L))
                .thenReturn(Optional.of(completedHandover));

        mockParticipant(participantB);

        assertThatThrownBy(() ->
                service.createOperatorChange(
                        1L, 20L, 20L, plannedDate, null))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void createOperatorChange_이전_인수인계가_완료되지_않으면_생성할_수_없다() {
        mockProjectOperation();
        mockNoActiveHandover();

        ProjectOperationHandover previous =
                ProjectOperationHandover.builder()
                        .id(2L)
                        .status(ProjectOperationHandoverStatus.REVIEW)
                        .sequenceNo(2)
                        .toParticipant(participantB)
                        .build();

        when(handoverRepository
                .findFirstByProjectOperation_IdOrderBySequenceNoDesc(1L))
                .thenReturn(Optional.of(previous));

        assertThatThrownBy(() ->
                service.createOperatorChange(
                        1L, 20L, 30L, plannedDate, null))
                .isInstanceOf(BusinessException.class);

        verifyNoInteractions(participantRepository);
    }

    @Test
    void createReHandover_최초_재인수인계에서_인계자를_지정한다() {
        mockProjectOperation();
        mockNoActiveHandover();

        when(handoverRepository
                .findFirstByProjectOperation_IdOrderBySequenceNoDesc(1L))
                .thenReturn(Optional.of(initialHandover));

        mockParticipant(participantA);
        mockParticipant(participantB);

        when(handoverRepository.save(any(ProjectOperationHandover.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProjectOperationHandover result = service.createReHandover(
                1L, 10L, 20L, plannedDate, "최초 재인수인계");

        assertThat(result.getHandoverType())
                .isEqualTo(ProjectOperationHandoverType.RE_HANDOVER);
        assertThat(result.getSequenceNo()).isEqualTo(2);
        assertThat(result.getFromParticipant()).isEqualTo(participantA);
        assertThat(result.getToParticipant()).isEqualTo(participantB);
        assertThat(result.getPreviousHandover()).isEqualTo(initialHandover);
    }

    @Test
    void createReHandover_기존_A에서_B의_참여자_관계를_유지한다() {
        mockProjectOperation();
        mockNoActiveHandover();

        when(handoverRepository
                .findFirstByProjectOperation_IdOrderBySequenceNoDesc(1L))
                .thenReturn(Optional.of(completedHandover));

        mockParticipant(participantA);
        mockParticipant(participantB);

        when(handoverRepository.save(any(ProjectOperationHandover.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProjectOperationHandover result = service.createReHandover(
                1L, 10L, 20L, plannedDate, "동일 운영자 재인수인계");

        assertThat(result.getFromParticipant()).isEqualTo(participantA);
        assertThat(result.getToParticipant()).isEqualTo(participantB);
        assertThat(result.getPreviousHandover()).isEqualTo(completedHandover);
    }

    @Test
    void createReHandover_이전과_다른_인수자를_지정할_수_없다() {
        mockProjectOperation();
        mockNoActiveHandover();

        when(handoverRepository
                .findFirstByProjectOperation_IdOrderBySequenceNoDesc(1L))
                .thenReturn(Optional.of(completedHandover));

        mockParticipant(participantA);
        mockParticipant(participantC);

        assertThatThrownBy(() ->
                service.createReHandover(
                        1L, 10L, 30L, plannedDate, null))
                .isInstanceOf(BusinessException.class);

        verify(handoverRepository, never())
                .save(any(ProjectOperationHandover.class));
    }

    @Test
    void createInitial_운영_서비스가_다른_참여자는_사용할_수_없다() {
        Operation anotherOperation = Operation.builder()
                .id(2L)
                .operationCode("OP-002")
                .operationName("다른 운영 서비스")
                .operationType(OperationType.INFRASTRUCTURE_SERVICE)
                .build();

        OperationParticipant anotherParticipant = createParticipant(
                40L,
                anotherOperation,
                OperationParticipantRole.OPERATOR);

        mockProjectOperation();
        mockNoActiveHandover();

        when(handoverRepository
                .findAllByProjectOperation_IdOrderBySequenceNoAsc(1L))
                .thenReturn(List.of());

        mockParticipant(anotherParticipant);

        assertThatThrownBy(() ->
                service.createInitial(1L, 40L, plannedDate, null))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void createInitial_허용되지_않는_참여자_역할이면_예외가_발생한다() {
        OperationParticipant customer = createParticipant(
                50L,
                operation,
                OperationParticipantRole.CUSTOMER);

        mockProjectOperation();
        mockNoActiveHandover();

        when(handoverRepository
                .findAllByProjectOperation_IdOrderBySequenceNoAsc(1L))
                .thenReturn(List.of());

        mockParticipant(customer);

        assertThatThrownBy(() ->
                service.createInitial(1L, 50L, plannedDate, null))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void createInitial_참여_시작일보다_빠른_예정일이면_예외가_발생한다() {
        OperationParticipant futureParticipant =
                OperationParticipant.builder()
                        .id(60L)
                        .operation(operation)
                        .participantRole(OperationParticipantRole.OPERATOR)
                        .effectiveFrom(LocalDate.of(2027, 1, 1))
                        .build();

        mockProjectOperation();
        mockNoActiveHandover();

        when(handoverRepository
                .findAllByProjectOperation_IdOrderBySequenceNoAsc(1L))
                .thenReturn(List.of());

        mockParticipant(futureParticipant);

        assertThatThrownBy(() ->
                service.createInitial(1L, 60L, plannedDate, null))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void request_활성_사용자로_인수인계를_요청한다() {
        ProjectOperationHandover handover =
                ProjectOperationHandover.builder()
                        .id(10L)
                        .status(ProjectOperationHandoverStatus.DRAFT)
                        .build();

        mockHandover(handover);

        when(userRepository.findById(100L))
                .thenReturn(Optional.of(user));

        ProjectOperationHandover result =
                service.request(10L, 100L, requestedAt);

        assertThat(result.getStatus())
                .isEqualTo(ProjectOperationHandoverStatus.REQUESTED);
        assertThat(result.getHandedOverBy()).isEqualTo(user);
        assertThat(result.getRequestedAt()).isEqualTo(requestedAt);
    }

    @Test
    void request_비활성_사용자는_요청자로_지정할_수_없다() {
        ProjectOperationHandover handover =
                ProjectOperationHandover.builder()
                        .id(10L)
                        .status(ProjectOperationHandoverStatus.DRAFT)
                        .build();

        User disabledUser = User.builder()
                .id(100L)
                .enabled(false)
                .build();

        mockHandover(handover);

        when(userRepository.findById(100L))
                .thenReturn(Optional.of(disabledUser));

        assertThatThrownBy(() ->
                service.request(10L, 100L, requestedAt))
                .isInstanceOf(BusinessException.class);

        assertThat(handover.getStatus())
                .isEqualTo(ProjectOperationHandoverStatus.DRAFT);
    }

    @Test
    void request_요청_시각이_null이면_예외가_발생한다() {
        ProjectOperationHandover handover =
                ProjectOperationHandover.builder()
                        .id(10L)
                        .status(ProjectOperationHandoverStatus.DRAFT)
                        .build();

        mockHandover(handover);

        when(userRepository.findById(100L))
                .thenReturn(Optional.of(user));

        assertThatThrownBy(() ->
                service.request(10L, 100L, null))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void start_REQUESTED_상태에서_작업을_시작한다() {
        ProjectOperationHandover handover =
                ProjectOperationHandover.builder()
                        .id(10L)
                        .status(ProjectOperationHandoverStatus.REQUESTED)
                        .build();

        mockHandover(handover);

        ProjectOperationHandover result = service.start(10L);

        assertThat(result.getStatus())
                .isEqualTo(ProjectOperationHandoverStatus.IN_PROGRESS);
    }

    @Test
    void start_DRAFT_상태에서는_작업을_시작할_수_없다() {
        ProjectOperationHandover handover =
                ProjectOperationHandover.builder()
                        .id(10L)
                        .status(ProjectOperationHandoverStatus.DRAFT)
                        .build();

        mockHandover(handover);

        assertThatThrownBy(() -> service.start(10L))
                .isInstanceOf(BusinessException.class);

        assertThat(handover.getStatus())
                .isEqualTo(ProjectOperationHandoverStatus.DRAFT);
    }

    @Test
    void submitForReview_IN_PROGRESS에서_REVIEW로_변경한다() {
        ProjectOperationHandover handover =
                ProjectOperationHandover.builder()
                        .id(10L)
                        .status(ProjectOperationHandoverStatus.IN_PROGRESS)
                        .build();

        mockHandover(handover);

        ProjectOperationHandover result =
                service.submitForReview(10L);

        assertThat(result.getStatus())
                .isEqualTo(ProjectOperationHandoverStatus.REVIEW);
    }

    @Test
    void accept_REVIEW_상태에서_인수인계를_승인한다() {
        ProjectOperationHandover handover =
                ProjectOperationHandover.builder()
                        .id(10L)
                        .status(ProjectOperationHandoverStatus.REVIEW)
                        .requestedAt(requestedAt)
                        .build();

        LocalDateTime acceptedAt = requestedAt.plusDays(1);

        mockHandover(handover);

        when(userRepository.findById(100L))
                .thenReturn(Optional.of(user));

        ProjectOperationHandover result =
                service.accept(10L, 100L, acceptedAt);

        assertThat(result.getStatus())
                .isEqualTo(ProjectOperationHandoverStatus.ACCEPTED);
        assertThat(result.getAcceptedBy()).isEqualTo(user);
        assertThat(result.getAcceptedAt()).isEqualTo(acceptedAt);
    }

    @Test
    void accept_요청_시각보다_빠른_승인_시각이면_예외가_발생한다() {
        ProjectOperationHandover handover =
                ProjectOperationHandover.builder()
                        .id(10L)
                        .status(ProjectOperationHandoverStatus.REVIEW)
                        .requestedAt(requestedAt)
                        .build();

        mockHandover(handover);

        when(userRepository.findById(100L))
                .thenReturn(Optional.of(user));

        assertThatThrownBy(() ->
                service.accept(10L, 100L, requestedAt.minusMinutes(1)))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void complete_ACCEPTED_상태에서_인수인계를_완료한다() {
        LocalDateTime acceptedAt = requestedAt.plusDays(1);
        LocalDateTime completedAt = acceptedAt.plusDays(1);

        ProjectOperationHandover handover =
                ProjectOperationHandover.builder()
                        .id(10L)
                        .status(ProjectOperationHandoverStatus.ACCEPTED)
                        .requestedAt(requestedAt)
                        .acceptedAt(acceptedAt)
                        .build();

        mockHandover(handover);

        ProjectOperationHandover result =
                service.complete(10L, completedAt);

        assertThat(result.getStatus())
                .isEqualTo(ProjectOperationHandoverStatus.COMPLETED);
        assertThat(result.getCompletedAt()).isEqualTo(completedAt);
    }

    @Test
    void complete_승인_시각보다_빠른_완료_시각이면_예외가_발생한다() {
        LocalDateTime acceptedAt = requestedAt.plusDays(1);

        ProjectOperationHandover handover =
                ProjectOperationHandover.builder()
                        .id(10L)
                        .status(ProjectOperationHandoverStatus.ACCEPTED)
                        .acceptedAt(acceptedAt)
                        .build();

        mockHandover(handover);

        assertThatThrownBy(() ->
                service.complete(10L, acceptedAt.minusMinutes(1)))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void reject_DRAFT_상태의_인수인계를_반려한다() {
        ProjectOperationHandover handover =
                ProjectOperationHandover.builder()
                        .id(10L)
                        .status(ProjectOperationHandoverStatus.DRAFT)
                        .build();

        mockHandover(handover);

        ProjectOperationHandover result = service.reject(10L);

        assertThat(result.getStatus())
                .isEqualTo(ProjectOperationHandoverStatus.REJECTED);
    }

    @Test
    void cancel_DRAFT_상태의_인수인계를_취소한다() {
        ProjectOperationHandover handover =
                ProjectOperationHandover.builder()
                        .id(10L)
                        .status(ProjectOperationHandoverStatus.DRAFT)
                        .build();

        mockHandover(handover);

        ProjectOperationHandover result = service.cancel(10L);

        assertThat(result.getStatus())
                .isEqualTo(ProjectOperationHandoverStatus.CANCELLED);
    }

    @Test
    void reject_COMPLETED_상태의_인수인계는_반려할_수_없다() {
        mockHandover(completedHandover);

        assertThatThrownBy(() -> service.reject(2L))
                .isInstanceOf(BusinessException.class);

        assertThat(completedHandover.getStatus())
                .isEqualTo(ProjectOperationHandoverStatus.COMPLETED);
    }

    @Test
    void getHistory_순번순으로_인수인계_이력을_조회한다() {
        List<ProjectOperationHandover> history =
                List.of(initialHandover, completedHandover);

        mockProjectOperation();

        when(handoverRepository
                .findAllByProjectOperation_IdOrderBySequenceNoAsc(1L))
                .thenReturn(history);

        List<ProjectOperationHandover> result = service.getHistory(1L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0)).isEqualTo(initialHandover);
        assertThat(result.get(1)).isEqualTo(completedHandover);

        verify(handoverRepository)
                .findAllByProjectOperation_IdOrderBySequenceNoAsc(1L);
    }

    @Test
    void getHistory_프로젝트_운영_관계가_없으면_예외가_발생한다() {
        when(projectOperationRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getHistory(999L))
                .isInstanceOf(BusinessException.class);

        verify(handoverRepository, never())
                .findAllByProjectOperation_IdOrderBySequenceNoAsc(any());
    }

    // ---------------------------------------------------------------------
    // Response wrapper tests
    // ---------------------------------------------------------------------

    @Test
    void createInitialResponse_생성된_인수인계를_응답으로_변환한다() {
        mockProjectOperation();
        mockNoActiveHandover();

        when(handoverRepository
                .findAllByProjectOperation_IdOrderBySequenceNoAsc(1L))
                .thenReturn(List.of());

        mockParticipant(participantB);

        when(handoverRepository.save(any(ProjectOperationHandover.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProjectOperationHandoverResponse response = createResponse(10L);

        when(handoverMapper.toResponse(any(ProjectOperationHandover.class)))
                .thenReturn(response);

        ProjectOperationHandoverResponse result =
                service.createInitialResponse(
                        1L, 20L, plannedDate, "최초 운영 인수인계");

        assertThat(result).isSameAs(response);
        verify(handoverMapper).toResponse(any(ProjectOperationHandover.class));
    }

    @Test
    void createOperatorChangeResponse_생성된_운영자_변경을_응답으로_변환한다() {
        mockProjectOperation();
        mockNoActiveHandover();

        when(handoverRepository
                .findFirstByProjectOperation_IdOrderBySequenceNoDesc(1L))
                .thenReturn(Optional.of(completedHandover));

        mockParticipant(participantB);
        mockParticipant(participantC);

        when(handoverRepository.save(any(ProjectOperationHandover.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProjectOperationHandoverResponse response = createResponse(11L);

        when(handoverMapper.toResponse(any(ProjectOperationHandover.class)))
                .thenReturn(response);

        ProjectOperationHandoverResponse result =
                service.createOperatorChangeResponse(
                        1L, 20L, 30L, plannedDate, "운영자 변경");

        assertThat(result).isSameAs(response);
        verify(handoverMapper).toResponse(any(ProjectOperationHandover.class));
    }

    @Test
    void createReHandoverResponse_생성된_재인수인계를_응답으로_변환한다() {
        mockProjectOperation();
        mockNoActiveHandover();

        when(handoverRepository
                .findFirstByProjectOperation_IdOrderBySequenceNoDesc(1L))
                .thenReturn(Optional.of(initialHandover));

        mockParticipant(participantA);
        mockParticipant(participantB);

        when(handoverRepository.save(any(ProjectOperationHandover.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProjectOperationHandoverResponse response = createResponse(12L);

        when(handoverMapper.toResponse(any(ProjectOperationHandover.class)))
                .thenReturn(response);

        ProjectOperationHandoverResponse result =
                service.createReHandoverResponse(
                        1L, 10L, 20L, plannedDate, "재인수인계");

        assertThat(result).isSameAs(response);
        verify(handoverMapper).toResponse(any(ProjectOperationHandover.class));
    }

    @Test
    void requestResponse_요청_처리_후_응답으로_변환한다() {
        ProjectOperationHandover handover =
                ProjectOperationHandover.builder()
                        .id(10L)
                        .status(ProjectOperationHandoverStatus.DRAFT)
                        .build();

        mockHandover(handover);

        when(userRepository.findById(100L))
                .thenReturn(Optional.of(user));

        ProjectOperationHandoverResponse response = createResponse(10L);

        when(handoverMapper.toResponse(handover))
                .thenReturn(response);

        ProjectOperationHandoverResponse result =
                service.requestResponse(10L, 100L, requestedAt);

        assertThat(result).isSameAs(response);
        assertThat(handover.getStatus())
                .isEqualTo(ProjectOperationHandoverStatus.REQUESTED);
        verify(handoverMapper).toResponse(handover);
    }

    @Test
    void startResponse_작업_시작_후_응답으로_변환한다() {
        ProjectOperationHandover handover =
                ProjectOperationHandover.builder()
                        .id(10L)
                        .status(ProjectOperationHandoverStatus.REQUESTED)
                        .build();

        mockHandover(handover);

        ProjectOperationHandoverResponse response = createResponse(10L);

        when(handoverMapper.toResponse(handover))
                .thenReturn(response);

        ProjectOperationHandoverResponse result =
                service.startResponse(10L);

        assertThat(result).isSameAs(response);
        assertThat(handover.getStatus())
                .isEqualTo(ProjectOperationHandoverStatus.IN_PROGRESS);
        verify(handoverMapper).toResponse(handover);
    }

    @Test
    void submitForReviewResponse_검토_요청_후_응답으로_변환한다() {
        ProjectOperationHandover handover =
                ProjectOperationHandover.builder()
                        .id(10L)
                        .status(ProjectOperationHandoverStatus.IN_PROGRESS)
                        .build();

        mockHandover(handover);

        ProjectOperationHandoverResponse response = createResponse(10L);

        when(handoverMapper.toResponse(handover))
                .thenReturn(response);

        ProjectOperationHandoverResponse result =
                service.submitForReviewResponse(10L);

        assertThat(result).isSameAs(response);
        assertThat(handover.getStatus())
                .isEqualTo(ProjectOperationHandoverStatus.REVIEW);
        verify(handoverMapper).toResponse(handover);
    }

    @Test
    void acceptResponse_승인_처리_후_응답으로_변환한다() {
        ProjectOperationHandover handover =
                ProjectOperationHandover.builder()
                        .id(10L)
                        .status(ProjectOperationHandoverStatus.REVIEW)
                        .requestedAt(requestedAt)
                        .build();

        LocalDateTime acceptedAt = requestedAt.plusDays(1);

        mockHandover(handover);

        when(userRepository.findById(100L))
                .thenReturn(Optional.of(user));

        ProjectOperationHandoverResponse response = createResponse(10L);

        when(handoverMapper.toResponse(handover))
                .thenReturn(response);

        ProjectOperationHandoverResponse result =
                service.acceptResponse(10L, 100L, acceptedAt);

        assertThat(result).isSameAs(response);
        assertThat(handover.getStatus())
                .isEqualTo(ProjectOperationHandoverStatus.ACCEPTED);
        verify(handoverMapper).toResponse(handover);
    }

    @Test
    void completeResponse_완료_처리_후_응답으로_변환한다() {
        LocalDateTime acceptedAt = requestedAt.plusDays(1);
        LocalDateTime completedAt = acceptedAt.plusDays(1);

        ProjectOperationHandover handover =
                ProjectOperationHandover.builder()
                        .id(10L)
                        .status(ProjectOperationHandoverStatus.ACCEPTED)
                        .requestedAt(requestedAt)
                        .acceptedAt(acceptedAt)
                        .build();

        mockHandover(handover);

        ProjectOperationHandoverResponse response = createResponse(10L);

        when(handoverMapper.toResponse(handover))
                .thenReturn(response);

        ProjectOperationHandoverResponse result =
                service.completeResponse(10L, completedAt);

        assertThat(result).isSameAs(response);
        assertThat(handover.getStatus())
                .isEqualTo(ProjectOperationHandoverStatus.COMPLETED);
        verify(handoverMapper).toResponse(handover);
    }

    @Test
    void rejectResponse_반려_처리_후_응답으로_변환한다() {
        ProjectOperationHandover handover =
                ProjectOperationHandover.builder()
                        .id(10L)
                        .status(ProjectOperationHandoverStatus.DRAFT)
                        .build();

        mockHandover(handover);

        ProjectOperationHandoverResponse response = createResponse(10L);

        when(handoverMapper.toResponse(handover))
                .thenReturn(response);

        ProjectOperationHandoverResponse result =
                service.rejectResponse(10L);

        assertThat(result).isSameAs(response);
        assertThat(handover.getStatus())
                .isEqualTo(ProjectOperationHandoverStatus.REJECTED);
        verify(handoverMapper).toResponse(handover);
    }

    @Test
    void cancelResponse_취소_처리_후_응답으로_변환한다() {
        ProjectOperationHandover handover =
                ProjectOperationHandover.builder()
                        .id(10L)
                        .status(ProjectOperationHandoverStatus.DRAFT)
                        .build();

        mockHandover(handover);

        ProjectOperationHandoverResponse response = createResponse(10L);

        when(handoverMapper.toResponse(handover))
                .thenReturn(response);

        ProjectOperationHandoverResponse result =
                service.cancelResponse(10L);

        assertThat(result).isSameAs(response);
        assertThat(handover.getStatus())
                .isEqualTo(ProjectOperationHandoverStatus.CANCELLED);
        verify(handoverMapper).toResponse(handover);
    }

    @Test
    void getDetailResponse_상세_조회_결과를_응답으로_변환한다() {
        mockHandover(completedHandover);

        ProjectOperationHandoverResponse response = createResponse(2L);

        when(handoverMapper.toResponse(completedHandover))
                .thenReturn(response);

        ProjectOperationHandoverResponse result =
                service.getDetailResponse(2L);

        assertThat(result).isSameAs(response);
        verify(handoverMapper).toResponse(completedHandover);
    }

    @Test
    void getHistoryResponses_전체_이력을_응답_목록으로_변환한다() {
        List<ProjectOperationHandover> history =
                List.of(initialHandover, completedHandover);

        mockProjectOperation();

        when(handoverRepository
                .findAllByProjectOperation_IdOrderBySequenceNoAsc(1L))
                .thenReturn(history);

        ProjectOperationHandoverResponse initialResponse = createResponse(1L);
        ProjectOperationHandoverResponse completedResponse = createResponse(2L);

        when(handoverMapper.toResponse(initialHandover))
                .thenReturn(initialResponse);

        when(handoverMapper.toResponse(completedHandover))
                .thenReturn(completedResponse);

        List<ProjectOperationHandoverResponse> result =
                service.getHistoryResponses(1L);

        assertThat(result)
                .containsExactly(initialResponse, completedResponse);

        verify(handoverMapper).toResponse(initialHandover);
        verify(handoverMapper).toResponse(completedHandover);
    }
}
