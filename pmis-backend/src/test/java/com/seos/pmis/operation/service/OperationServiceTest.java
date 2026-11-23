
package com.seos.pmis.operation.service;

import com.seos.common.exception.BusinessException;
import com.seos.pmis.operation.dto.response.OperationResponse;
import com.seos.pmis.operation.entity.Operation;
import com.seos.pmis.operation.entity.OperationStatus;
import com.seos.pmis.operation.entity.OperationType;
import com.seos.pmis.operation.mapper.OperationMapper;
import com.seos.pmis.operation.repository.OperationRepository;
import com.seos.pmis.user.entity.User;
import com.seos.pmis.user.entity.UserRole;
import com.seos.pmis.user.repository.UserRepository;
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
@DisplayName("OperationService 테스트")
class OperationServiceTest {

    @Mock
    private OperationRepository operationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OperationMapper operationMapper;

    @InjectMocks
    private OperationService operationService;

    private Operation operation;
    private User manager;

    private final LocalDate startDate = LocalDate.of(2026, 1, 1);
    private final LocalDate endDate = LocalDate.of(2026, 12, 31);

    @BeforeEach
    void setUp() {
        manager = User.builder()
                .id(10L)
                .username("manager")
                .name("운영 담당자")
                .email("manager@example.com")
                .password("encoded-password")
                .role(UserRole.ROLE_USER)
                .enabled(true)
                .build();

        operation = Operation.builder()
                .id(1L)
                .operationCode("OP-001")
                .operationName("테스트 운영 서비스")
                .operationType(OperationType.INFRASTRUCTURE_SERVICE)
                .status(OperationStatus.ACTIVE)
                .manager(manager)
                .description("운영 서비스 설명")
                .effectiveFrom(startDate)
                .effectiveTo(endDate)
                .build();
    }

    // ---------------------------------------------------------------------
    // Create
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("운영 서비스를 생성한다")
    void create_success() {
        when(operationRepository.existsByOperationCode("OP-001"))
                .thenReturn(false);
        when(userRepository.findById(10L))
                .thenReturn(Optional.of(manager));
        when(operationRepository.save(any(Operation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Operation result = operationService.create(
                "OP-001",
                "테스트 운영 서비스",
                OperationType.INFRASTRUCTURE_SERVICE,
                10L,
                "운영 서비스 설명",
                startDate,
                endDate);

        assertThat(result.getOperationCode()).isEqualTo("OP-001");
        assertThat(result.getOperationName()).isEqualTo("테스트 운영 서비스");
        assertThat(result.getOperationType())
                .isEqualTo(OperationType.INFRASTRUCTURE_SERVICE);
        assertThat(result.getManager()).isEqualTo(manager);
        assertThat(result.getDescription()).isEqualTo("운영 서비스 설명");
        assertThat(result.getEffectiveFrom()).isEqualTo(startDate);
        assertThat(result.getEffectiveTo()).isEqualTo(endDate);

        verify(operationRepository).save(any(Operation.class));
    }

    @Test
    @DisplayName("담당자가 없어도 운영 서비스를 생성할 수 있다")
    void create_withoutManager() {
        when(operationRepository.existsByOperationCode("OP-001"))
                .thenReturn(false);
        when(operationRepository.save(any(Operation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Operation result = operationService.create(
                "OP-001",
                "담당자 없는 운영 서비스",
                OperationType.OTHER,
                null,
                null,
                startDate,
                null);

        assertThat(result.getManager()).isNull();

        verify(userRepository, never()).findById(any());
        verify(operationRepository).save(any(Operation.class));
    }

    @Test
    @DisplayName("중복된 운영 코드로 생성할 수 없다")
    void create_duplicateCode() {
        when(operationRepository.existsByOperationCode("OP-001"))
                .thenReturn(true);

        assertThatThrownBy(() -> operationService.create(
                "OP-001",
                "중복 운영 서비스",
                OperationType.OTHER,
                null,
                null,
                startDate,
                null))
                .isInstanceOf(BusinessException.class);

        verify(operationRepository, never()).save(any(Operation.class));
        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("존재하지 않는 담당자를 지정하면 생성할 수 없다")
    void create_managerNotFound() {
        when(operationRepository.existsByOperationCode("OP-001"))
                .thenReturn(false);
        when(userRepository.findById(10L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> operationService.create(
                "OP-001",
                "테스트 운영 서비스",
                OperationType.OTHER,
                10L,
                null,
                startDate,
                null))
                .isInstanceOf(BusinessException.class);

        verify(operationRepository, never()).save(any(Operation.class));
    }

    @Test
    @DisplayName("종료일이 시작일보다 빠르면 생성할 수 없다")
    void create_invalidPeriod() {
        assertThatThrownBy(() -> operationService.create(
                "OP-001",
                "테스트 운영 서비스",
                OperationType.OTHER,
                null,
                null,
                endDate,
                startDate))
                .isInstanceOf(BusinessException.class);

        verifyNoInteractions(
                operationRepository,
                userRepository);
    }

    @Test
    @DisplayName("종료일이 있는데 시작일이 없으면 생성할 수 없다")
    void create_missingStartDate() {
        assertThatThrownBy(() -> operationService.create(
                "OP-001",
                "테스트 운영 서비스",
                OperationType.OTHER,
                null,
                null,
                null,
                endDate))
                .isInstanceOf(BusinessException.class);

        verifyNoInteractions(
                operationRepository,
                userRepository);
    }

    // ---------------------------------------------------------------------
    // Update
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("운영 서비스 정보를 수정한다")
    void update_success() {
        when(operationRepository.findById(1L))
                .thenReturn(Optional.of(operation));
        when(userRepository.findById(10L))
                .thenReturn(Optional.of(manager));

        LocalDate newStartDate = LocalDate.of(2026, 2, 1);
        LocalDate newEndDate = LocalDate.of(2026, 11, 30);

        Operation result = operationService.update(
                1L,
                "수정된 운영 서비스",
                OperationType.PLATFORM_SERVICE,
                10L,
                "수정된 설명",
                newStartDate,
                newEndDate);

        assertThat(result.getOperationName()).isEqualTo("수정된 운영 서비스");
        assertThat(result.getOperationType())
                .isEqualTo(OperationType.PLATFORM_SERVICE);
        assertThat(result.getManager()).isEqualTo(manager);
        assertThat(result.getDescription()).isEqualTo("수정된 설명");
        assertThat(result.getEffectiveFrom()).isEqualTo(newStartDate);
        assertThat(result.getEffectiveTo()).isEqualTo(newEndDate);

        // 운영 코드는 수정 대상이 아니다.
        assertThat(result.getOperationCode()).isEqualTo("OP-001");
    }

    @Test
    @DisplayName("담당자 ID가 null이면 기존 담당자를 해제한다")
    void update_clearManager() {
        when(operationRepository.findById(1L))
                .thenReturn(Optional.of(operation));

        Operation result = operationService.update(
                1L,
                "담당자 해제 운영 서비스",
                OperationType.OTHER,
                null,
                null,
                startDate,
                endDate);

        assertThat(result.getManager()).isNull();
        verify(userRepository, never()).findById(any());
    }

    @Test
    @DisplayName("존재하지 않는 담당자로 수정할 수 없다")
    void update_managerNotFound() {
        when(operationRepository.findById(1L))
                .thenReturn(Optional.of(operation));
        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> operationService.update(
                1L,
                "수정된 운영 서비스",
                OperationType.OTHER,
                99L,
                null,
                startDate,
                endDate))
                .isInstanceOf(BusinessException.class);

        assertThat(operation.getOperationName())
                .isEqualTo("테스트 운영 서비스");
    }

    @Test
    @DisplayName("기간이 잘못되면 기존 정보를 유지한다")
    void update_invalidPeriod() {
        when(operationRepository.findById(1L))
                .thenReturn(Optional.of(operation));

        assertThatThrownBy(() -> operationService.update(
                1L,
                "수정된 운영 서비스",
                OperationType.OTHER,
                null,
                null,
                endDate,
                startDate))
                .isInstanceOf(BusinessException.class);

        assertThat(operation.getOperationName())
                .isEqualTo("테스트 운영 서비스");
        assertThat(operation.getEffectiveFrom()).isEqualTo(startDate);
        assertThat(operation.getEffectiveTo()).isEqualTo(endDate);

        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("존재하지 않는 운영 서비스를 수정할 수 없다")
    void update_operationNotFound() {
        when(operationRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> operationService.update(
                1L,
                "수정된 운영 서비스",
                OperationType.OTHER,
                null,
                null,
                startDate,
                endDate))
                .isInstanceOf(BusinessException.class);

        verifyNoInteractions(userRepository);
    }

    // ---------------------------------------------------------------------
    // Change status
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("운영 서비스 상태를 변경한다")
    void changeStatus_success() {
        when(operationRepository.findById(1L))
                .thenReturn(Optional.of(operation));

        Operation result = operationService.changeStatus(
                1L,
                OperationStatus.SUSPENDED);

        assertThat(result.getStatus()).isEqualTo(OperationStatus.SUSPENDED);
    }

    @Test
    @DisplayName("상태가 null이면 변경할 수 없다")
    void changeStatus_nullStatus() {
        when(operationRepository.findById(1L))
                .thenReturn(Optional.of(operation));

        assertThatThrownBy(() -> operationService.changeStatus(1L, null))
                .isInstanceOf(BusinessException.class);

        assertThat(operation.getStatus()).isEqualTo(OperationStatus.ACTIVE);
    }

    @Test
    @DisplayName("존재하지 않는 운영 서비스의 상태를 변경할 수 없다")
    void changeStatus_operationNotFound() {
        when(operationRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> operationService.changeStatus(
                1L,
                OperationStatus.CLOSED))
                .isInstanceOf(BusinessException.class);
    }

    // ---------------------------------------------------------------------
    // Read
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("운영 서비스 상세 정보를 조회한다")
    void getDetail_success() {
        when(operationRepository.findById(1L))
                .thenReturn(Optional.of(operation));

        Operation result = operationService.getDetail(1L);

        assertThat(result).isEqualTo(operation);
    }

    @Test
    @DisplayName("존재하지 않는 운영 서비스 상세 조회는 예외가 발생한다")
    void getDetail_notFound() {
        when(operationRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> operationService.getDetail(1L))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("운영 코드로 운영 서비스를 조회한다")
    void findByCode_success() {
        when(operationRepository.findByOperationCode("OP-001"))
                .thenReturn(Optional.of(operation));

        Operation result = operationService.findByCode("OP-001");

        assertThat(result).isEqualTo(operation);
    }

    @Test
    @DisplayName("존재하지 않는 운영 코드로 조회하면 예외가 발생한다")
    void findByCode_notFound() {
        when(operationRepository.findByOperationCode("UNKNOWN"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> operationService.findByCode("UNKNOWN"))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("운영 코드가 비어 있으면 조회할 수 없다")
    void findByCode_blankCode() {
        assertThatThrownBy(() -> operationService.findByCode(" "))
                .isInstanceOf(BusinessException.class);

        verify(operationRepository, never()).findByOperationCode(any());
    }

    @Test
    @DisplayName("전체 운영 서비스 목록을 조회한다")
    void findAll_success() {
        List<Operation> expected = List.of(operation);

        when(operationRepository.findAll()).thenReturn(expected);

        List<Operation> result = operationService.findAll();

        assertThat(result).containsExactlyElementsOf(expected);
    }

    @Test
    @DisplayName("등록된 운영 서비스가 없으면 빈 목록을 반환한다")
    void findAll_empty() {
        when(operationRepository.findAll()).thenReturn(List.of());

        List<Operation> result = operationService.findAll();

        assertThat(result).isEmpty();
    }

    // ---------------------------------------------------------------------
    // Response
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("운영 서비스 생성 결과를 Response DTO로 반환한다")
    void createResponse_success() {
        OperationResponse expected = OperationResponse.builder()
                .id(1L)
                .operationCode("OP-001")
                .operationName("테스트 운영 서비스")
                .build();

        when(operationRepository.existsByOperationCode("OP-001"))
                .thenReturn(false);
        when(userRepository.findById(10L))
                .thenReturn(Optional.of(manager));
        when(operationRepository.save(any(Operation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(operationMapper.toResponse(any(Operation.class)))
                .thenReturn(expected);

        OperationResponse result = operationService.createResponse(
                "OP-001",
                "테스트 운영 서비스",
                OperationType.INFRASTRUCTURE_SERVICE,
                10L,
                "운영 서비스 설명",
                startDate,
                endDate);

        assertThat(result).isEqualTo(expected);
        verify(operationMapper).toResponse(any(Operation.class));
    }

    @Test
    @DisplayName("운영 서비스 상세 조회 결과를 Response DTO로 반환한다")
    void getDetailResponse_success() {
        OperationResponse expected = OperationResponse.builder()
                .id(1L)
                .operationCode("OP-001")
                .build();

        when(operationRepository.findById(1L))
                .thenReturn(Optional.of(operation));
        when(operationMapper.toResponse(operation))
                .thenReturn(expected);

        OperationResponse result = operationService.getDetailResponse(1L);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("전체 운영 서비스 목록을 Response DTO 목록으로 반환한다")
    void findAllResponses_success() {
        OperationResponse expected = OperationResponse.builder()
                .id(1L)
                .operationCode("OP-001")
                .build();

        when(operationRepository.findAll()).thenReturn(List.of(operation));
        when(operationMapper.toResponse(operation)).thenReturn(expected);

        List<OperationResponse> result = operationService.findAllResponses();

        assertThat(result).containsExactly(expected);
    }
}
