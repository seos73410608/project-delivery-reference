
package com.seos.pmis.operationscope.service;

import com.seos.common.exception.BusinessException;
import com.seos.pmis.operation.entity.Operation;
import com.seos.pmis.operation.entity.OperationType;
import com.seos.pmis.operation.repository.OperationRepository;
import com.seos.pmis.operationscope.entity.OperationScope;
import com.seos.pmis.operationscope.entity.OperationScopeStatus;
import com.seos.pmis.operationscope.repository.OperationScopeRepository;
import com.seos.pmis.requirement.entity.OperationalScope;
import com.seos.pmis.requirement.entity.OperationalScopeType;
import com.seos.pmis.requirement.repository.OperationalScopeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OperationScopeService 테스트")
class OperationScopeServiceTest {

    @Mock
    private OperationScopeRepository operationScopeRepository;

    @Mock
    private OperationRepository operationRepository;

    @Mock
    private OperationalScopeRepository operationalScopeRepository;

    @InjectMocks
    private OperationScopeService operationScopeService;

    private Operation operation;
    private OperationalScope operationalScope;
    private OperationScope operationScope;

    private final LocalDate startDate = LocalDate.of(2026, 1, 1);
    private final LocalDate endDate = LocalDate.of(2026, 12, 31);

    @BeforeEach
    void setUp() {
        operation = Operation.builder()
                .id(1L)
                .operationCode("OP-001")
                .operationName("테스트 운영 서비스")
                .operationType(OperationType.INFRASTRUCTURE_SERVICE)
                .build();

        operationalScope = OperationalScope.builder()
                .id(1L)
                .scopeCode("SCP-001")
                .scopeName("테스트 운영 범위")
                .scopeType(OperationalScopeType.SYSTEM)
                .build();

        operationScope = OperationScope.builder()
                .id(1L)
                .operation(operation)
                .operationalScope(operationalScope)
                .status(OperationScopeStatus.PLANNED)
                .effectiveFrom(startDate)
                .description("초기 운영 범위 관계")
                .build();
    }

    // ---------------------------------------------------------------------
    // Create
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("운영 서비스와 운영 범위 관계를 생성한다")
    void create_success() {
        givenSourceEntitiesExist();
        givenNoActiveRelationship();

        when(operationScopeRepository.save(any(OperationScope.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OperationScope result = operationScopeService.create(
                1L,
                1L,
                startDate,
                null,
                "신규 운영 범위 관계");

        assertThat(result.getOperation()).isEqualTo(operation);
        assertThat(result.getOperationalScope()).isEqualTo(operationalScope);
        assertThat(result.getStatus()).isEqualTo(OperationScopeStatus.PLANNED);
        assertThat(result.getEffectiveFrom()).isEqualTo(startDate);
        assertThat(result.getEffectiveTo()).isNull();
        assertThat(result.getDescription()).isEqualTo("신규 운영 범위 관계");

        verify(operationScopeRepository).save(any(OperationScope.class));
    }

    @Test
    @DisplayName("종료일이 시작일보다 빠르면 관계를 생성하지 않는다")
    void create_invalidPeriod() {
        assertThatThrownBy(() -> operationScopeService.create(
                1L,
                1L,
                endDate,
                startDate,
                "잘못된 기간")).isInstanceOf(BusinessException.class);

        verifyNoInteractions(
                operationRepository,
                operationalScopeRepository,
                operationScopeRepository);
    }

    @Test
    @DisplayName("종료일이 있는데 시작일이 없으면 관계를 생성하지 않는다")
    void create_missingStartDate() {
        assertThatThrownBy(() -> operationScopeService.create(
                1L,
                1L,
                null,
                endDate,
                "시작일 누락")).isInstanceOf(BusinessException.class);

        verifyNoInteractions(
                operationRepository,
                operationalScopeRepository,
                operationScopeRepository);
    }

    @Test
    @DisplayName("운영 서비스가 존재하지 않으면 관계를 생성하지 않는다")
    void create_operationNotFound() {
        when(operationRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> operationScopeService.create(
                1L,
                1L,
                startDate,
                null,
                "운영 서비스 없음")).isInstanceOf(BusinessException.class);

        verifyNoInteractions(
                operationalScopeRepository,
                operationScopeRepository);
    }

    @Test
    @DisplayName("운영 범위가 존재하지 않으면 관계를 생성하지 않는다")
    void create_operationalScopeNotFound() {
        when(operationRepository.findById(1L))
                .thenReturn(Optional.of(operation));
        when(operationalScopeRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> operationScopeService.create(
                1L,
                1L,
                startDate,
                null,
                "운영 범위 없음")).isInstanceOf(BusinessException.class);

        verifyNoInteractions(operationScopeRepository);
    }

    @Test
    @DisplayName("동일한 PLANNED 관계가 있으면 중복 생성을 거부한다")
    void create_duplicatePlannedRelationship() {
        givenSourceEntitiesExist();

        when(operationScopeRepository
                .existsByOperation_IdAndOperationalScope_IdAndStatus(
                        1L, 1L, OperationScopeStatus.PLANNED))
                .thenReturn(true);

        when(operationScopeRepository
                .existsByOperation_IdAndOperationalScope_IdAndStatus(
                        1L, 1L, OperationScopeStatus.ACTIVE))
                .thenReturn(false);

        assertThatThrownBy(() -> operationScopeService.create(
                1L,
                1L,
                startDate,
                null,
                "중복 관계")).isInstanceOf(BusinessException.class);

        verify(operationScopeRepository, never())
                .save(any(OperationScope.class));
    }

    @Test
    @DisplayName("동일한 ACTIVE 관계가 있으면 중복 생성을 거부한다")
    void create_duplicateActiveRelationship() {
        givenSourceEntitiesExist();

        when(operationScopeRepository
                .existsByOperation_IdAndOperationalScope_IdAndStatus(
                        1L, 1L, OperationScopeStatus.PLANNED))
                .thenReturn(false);

        when(operationScopeRepository
                .existsByOperation_IdAndOperationalScope_IdAndStatus(
                        1L, 1L, OperationScopeStatus.ACTIVE))
                .thenReturn(true);

        assertThatThrownBy(() -> operationScopeService.create(
                1L,
                1L,
                startDate,
                null,
                "중복 관계")).isInstanceOf(BusinessException.class);

        verify(operationScopeRepository, never())
                .save(any(OperationScope.class));
    }

    @Test
    @DisplayName("종료된 과거 관계가 있으면 새로운 관계를 생성할 수 있다")
    void create_afterHistoricalRelationship() {
        givenSourceEntitiesExist();
        givenNoActiveRelationship();

        when(operationScopeRepository.save(any(OperationScope.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OperationScope result = operationScopeService.create(
                1L,
                1L,
                startDate,
                null,
                "새로운 운영 범위 관계");

        assertThat(result.getStatus()).isEqualTo(OperationScopeStatus.PLANNED);
        assertThat(result.getOperation()).isEqualTo(operation);
        assertThat(result.getOperationalScope()).isEqualTo(operationalScope);

        verify(operationScopeRepository).save(any(OperationScope.class));
    }

    // ---------------------------------------------------------------------
    // Update
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("운영 범위 관계 정보를 수정한다")
    void update_success() {
        when(operationScopeRepository.findById(1L))
                .thenReturn(Optional.of(operationScope));

        LocalDate newStartDate = LocalDate.of(2026, 2, 1);
        LocalDate newEndDate = LocalDate.of(2026, 11, 30);

        OperationScope result = operationScopeService.update(
                1L,
                newStartDate,
                newEndDate,
                "수정된 운영 범위 관계");

        assertThat(result.getEffectiveFrom()).isEqualTo(newStartDate);
        assertThat(result.getEffectiveTo()).isEqualTo(newEndDate);
        assertThat(result.getDescription()).isEqualTo("수정된 운영 범위 관계");

        // 관계의 양쪽 원본 엔티티는 변경하지 않는다.
        assertThat(result.getOperation()).isEqualTo(operation);
        assertThat(result.getOperationalScope()).isEqualTo(operationalScope);
    }

    @Test
    @DisplayName("기간이 잘못되면 기존 관계 정보를 유지한다")
    void update_invalidPeriod_preservesExistingValues() {
        when(operationScopeRepository.findById(1L))
                .thenReturn(Optional.of(operationScope));

        assertThatThrownBy(() -> operationScopeService.update(
                1L,
                endDate,
                startDate,
                "잘못된 기간")).isInstanceOf(BusinessException.class);

        assertThat(operationScope.getEffectiveFrom()).isEqualTo(startDate);
        assertThat(operationScope.getEffectiveTo()).isNull();
        assertThat(operationScope.getDescription()).isEqualTo("초기 운영 범위 관계");
    }

    @Test
    @DisplayName("관계 정보 수정 시 대상 관계가 없으면 예외가 발생한다")
    void update_operationScopeNotFound() {
        when(operationScopeRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> operationScopeService.update(
                1L,
                startDate,
                null,
                "수정")).isInstanceOf(BusinessException.class);
    }

    // ---------------------------------------------------------------------
    // Change status
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("PLANNED에서 ACTIVE로 변경한다")
    void changeStatus_plannedToActive() {
        givenOperationScopeExists();

        OperationScope result = operationScopeService.changeStatus(
                1L,
                OperationScopeStatus.ACTIVE);

        assertThat(result.getStatus()).isEqualTo(OperationScopeStatus.ACTIVE);
    }

    @Test
    @DisplayName("PLANNED에서 CANCELLED로 변경한다")
    void changeStatus_plannedToCancelled() {
        givenOperationScopeExists();

        OperationScope result = operationScopeService.changeStatus(
                1L,
                OperationScopeStatus.CANCELLED);

        assertThat(result.getStatus()).isEqualTo(OperationScopeStatus.CANCELLED);
    }

    @Test
    @DisplayName("ACTIVE에서 COMPLETED로 변경한다")
    void changeStatus_activeToCompleted() {
        operationScope = OperationScope.builder()
                .id(1L)
                .operation(operation)
                .operationalScope(operationalScope)
                .status(OperationScopeStatus.ACTIVE)
                .effectiveFrom(startDate)
                .build();

        givenOperationScopeExists();

        OperationScope result = operationScopeService.changeStatus(
                1L,
                OperationScopeStatus.COMPLETED);

        assertThat(result.getStatus()).isEqualTo(OperationScopeStatus.COMPLETED);
    }

    @Test
    @DisplayName("ACTIVE에서 CANCELLED로 변경한다")
    void changeStatus_activeToCancelled() {
        operationScope = OperationScope.builder()
                .id(1L)
                .operation(operation)
                .operationalScope(operationalScope)
                .status(OperationScopeStatus.ACTIVE)
                .effectiveFrom(startDate)
                .build();

        givenOperationScopeExists();

        OperationScope result = operationScopeService.changeStatus(
                1L,
                OperationScopeStatus.CANCELLED);

        assertThat(result.getStatus()).isEqualTo(OperationScopeStatus.CANCELLED);
    }

    @Test
    @DisplayName("상태가 null이면 변경을 거부한다")
    void changeStatus_nullStatus() {
        givenOperationScopeExists();

        assertThatThrownBy(() -> operationScopeService.changeStatus(1L, null))
                .isInstanceOf(BusinessException.class);

        assertThat(operationScope.getStatus())
                .isEqualTo(OperationScopeStatus.PLANNED);
    }

    @Test
    @DisplayName("허용되지 않은 상태 전이는 거부한다")
    void changeStatus_invalidTransition() {
        givenOperationScopeExists();

        assertThatThrownBy(() -> operationScopeService.changeStatus(
                1L,
                OperationScopeStatus.COMPLETED)).isInstanceOf(BusinessException.class);

        assertThat(operationScope.getStatus())
                .isEqualTo(OperationScopeStatus.PLANNED);
    }

    @Test
    @DisplayName("동일한 상태로 변경할 수 없다")
    void changeStatus_sameStatus() {
        givenOperationScopeExists();

        assertThatThrownBy(() -> operationScopeService.changeStatus(
                1L,
                OperationScopeStatus.PLANNED)).isInstanceOf(BusinessException.class);

        assertThat(operationScope.getStatus())
                .isEqualTo(OperationScopeStatus.PLANNED);
    }

    @Test
    @DisplayName("COMPLETED 상태는 다른 상태로 변경할 수 없다")
    void changeStatus_completedIsTerminal() {
        operationScope = OperationScope.builder()
                .id(1L)
                .operation(operation)
                .operationalScope(operationalScope)
                .status(OperationScopeStatus.COMPLETED)
                .effectiveFrom(startDate)
                .build();

        givenOperationScopeExists();

        assertThatThrownBy(() -> operationScopeService.changeStatus(
                1L,
                OperationScopeStatus.ACTIVE)).isInstanceOf(BusinessException.class);

        assertThat(operationScope.getStatus())
                .isEqualTo(OperationScopeStatus.COMPLETED);
    }

    @Test
    @DisplayName("CANCELLED 상태는 다른 상태로 변경할 수 없다")
    void changeStatus_cancelledIsTerminal() {
        operationScope = OperationScope.builder()
                .id(1L)
                .operation(operation)
                .operationalScope(operationalScope)
                .status(OperationScopeStatus.CANCELLED)
                .effectiveFrom(startDate)
                .build();

        givenOperationScopeExists();

        assertThatThrownBy(() -> operationScopeService.changeStatus(
                1L,
                OperationScopeStatus.ACTIVE)).isInstanceOf(BusinessException.class);

        assertThat(operationScope.getStatus())
                .isEqualTo(OperationScopeStatus.CANCELLED);
    }

    // ---------------------------------------------------------------------
    // Read
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("운영 범위 관계 상세 정보를 조회한다")
    void getDetail_success() {
        when(operationScopeRepository.findById(1L))
                .thenReturn(Optional.of(operationScope));

        OperationScope result = operationScopeService.getDetail(1L);

        assertThat(result).isEqualTo(operationScope);
    }

    @Test
    @DisplayName("존재하지 않는 운영 범위 관계 상세 조회는 예외가 발생한다")
    void getDetail_notFound() {
        when(operationScopeRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> operationScopeService.getDetail(1L))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("운영 서비스에 연결된 운영 범위 관계를 조회한다")
    void findByOperation_success() {
        List<OperationScope> expected = List.of(operationScope);

        when(operationRepository.findById(1L))
                .thenReturn(Optional.of(operation));
        when(operationScopeRepository.findAllByOperation_Id(1L))
                .thenReturn(expected);

        List<OperationScope> result = operationScopeService.findByOperation(1L);

        assertThat(result).hasSize(1);
        assertThat(result).containsExactlyElementsOf(expected);
    }

    @Test
    @DisplayName("운영 범위에 연결된 운영 서비스 관계를 조회한다")
    void findByOperationalScope_success() {
        List<OperationScope> expected = List.of(operationScope);

        when(operationalScopeRepository.findById(1L))
                .thenReturn(Optional.of(operationalScope));
        when(operationScopeRepository.findAllByOperationalScope_Id(1L))
                .thenReturn(expected);

        List<OperationScope> result = operationScopeService.findByOperationalScope(1L);

        assertThat(result).hasSize(1);
        assertThat(result).containsExactlyElementsOf(expected);
    }

    // ---------------------------------------------------------------------
    // Test helpers
    // ---------------------------------------------------------------------

    private void givenSourceEntitiesExist() {
        when(operationRepository.findById(1L))
                .thenReturn(Optional.of(operation));
        when(operationalScopeRepository.findById(1L))
                .thenReturn(Optional.of(operationalScope));
    }

    private void givenNoActiveRelationship() {
        when(operationScopeRepository
                .existsByOperation_IdAndOperationalScope_IdAndStatus(
                        1L, 1L, OperationScopeStatus.PLANNED))
                .thenReturn(false);

        when(operationScopeRepository
                .existsByOperation_IdAndOperationalScope_IdAndStatus(
                        1L, 1L, OperationScopeStatus.ACTIVE))
                .thenReturn(false);
    }

    private void givenOperationScopeExists() {
        when(operationScopeRepository.findById(1L))
                .thenReturn(Optional.of(operationScope));
    }
}
