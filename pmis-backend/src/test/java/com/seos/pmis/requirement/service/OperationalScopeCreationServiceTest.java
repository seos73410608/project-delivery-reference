package com.seos.pmis.requirement.service;

import com.seos.common.exception.BusinessException;
import com.seos.pmis.requirement.dto.request.OperationalScopeCreateRequest;
import com.seos.pmis.requirement.dto.response.OperationalScopeResponse;
import com.seos.pmis.requirement.entity.OperationalScope;
import com.seos.pmis.requirement.entity.OperationalScopeStatus;
import com.seos.pmis.requirement.entity.OperationalScopeType;
import com.seos.pmis.requirement.exception.OperationalScopeErrorCode;
import com.seos.pmis.requirement.mapper.OperationalScopeMapper;
import com.seos.pmis.requirement.repository.OperationalScopeRepository;
import com.seos.pmis.user.entity.User;
import com.seos.pmis.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OperationalScopeCreationServiceTest {

    @Mock
    private OperationalScopeRepository operationalScopeRepository;

    @Mock
    private UserRepository userRepository;

    private OperationalScopeCreationService operationalScopeCreationService;

    private OperationalScope parentScope;
    private User owner;

    @BeforeEach
    void setUp() {
        operationalScopeCreationService =
                new OperationalScopeCreationService(
                        operationalScopeRepository,
                        userRepository,
                        new OperationalScopeMapper()
                );

        parentScope = OperationalScope.builder()
                .id(20L)
                .scopeCode("OPS-000020")
                .scopeName("Operations Service")
                .scopeType(OperationalScopeType.SERVICE)
                .build();

        owner = User.builder()
                .id(10L)
                .username("manager")
                .name("Manager")
                .email("manager@example.com")
                .enabled(true)
                .build();
    }

    @Test
    void create_createsOperationalScopeSuccessfully() {
        // given
        when(operationalScopeRepository.nextScopeCodeSequenceValue())
                .thenReturn(1L);

        when(operationalScopeRepository.save(any(OperationalScope.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OperationalScopeCreateRequest request = createRequest(
                "Operations System",
                OperationalScopeType.SYSTEM,
                null,
                null,
                "Operations system description",
                null,
                null
        );

        // when
        OperationalScopeResponse result =
                operationalScopeCreationService.create(request);

        // then
        assertThat(result.getScopeCode()).isEqualTo("OPS-000001");
        assertThat(result.getScopeName()).isEqualTo("Operations System");
        assertThat(result.getScopeType()).isEqualTo(OperationalScopeType.SYSTEM);
        assertThat(result.getStatus()).isEqualTo(OperationalScopeStatus.PLANNED);
        assertThat(result.getParentScopeId()).isNull();
        assertThat(result.getOwnerId()).isNull();
        assertThat(result.getDescription())
                .isEqualTo("Operations system description");

        verify(operationalScopeRepository).save(any(OperationalScope.class));
        verify(operationalScopeRepository, never()).findById(any());
        verify(userRepository, never()).findById(any());
    }

    @Test
    void create_createsScopeWithParentAndOwner() {
        // given
        when(operationalScopeRepository.findById(20L))
                .thenReturn(Optional.of(parentScope));

        when(userRepository.findById(10L))
                .thenReturn(Optional.of(owner));

        when(operationalScopeRepository.nextScopeCodeSequenceValue())
                .thenReturn(21L);

        when(operationalScopeRepository.save(any(OperationalScope.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OperationalScopeCreateRequest request = createRequest(
                "Operations Database",
                OperationalScopeType.DATABASE,
                20L,
                10L,
                "Operations DB scope",
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31)
        );

        // when
        OperationalScopeResponse result =
                operationalScopeCreationService.create(request);

        // then
        assertThat(result.getScopeCode()).isEqualTo("OPS-000021");
        assertThat(result.getParentScopeId()).isEqualTo(20L);
        assertThat(result.getParentScopeCode()).isEqualTo("OPS-000020");
        assertThat(result.getParentScopeName()).isEqualTo("Operations Service");
        assertThat(result.getOwnerId()).isEqualTo(10L);
        assertThat(result.getOwnerUsername()).isEqualTo("manager");
        assertThat(result.getOwnerName()).isEqualTo("Manager");
        assertThat(result.getEffectiveFrom())
                .isEqualTo(LocalDate.of(2026, 1, 1));
        assertThat(result.getEffectiveTo())
                .isEqualTo(LocalDate.of(2026, 12, 31));

        verify(operationalScopeRepository).findById(20L);
        verify(userRepository).findById(10L);
        verify(operationalScopeRepository).save(any(OperationalScope.class));
    }

    @Test
    void createThrowsExceptionWhenParentScopeDoesNotExist() {
        // given
        when(operationalScopeRepository.findById(999L))
                .thenReturn(Optional.empty());

        OperationalScopeCreateRequest request = createRequest(
                "Operations System",
                OperationalScopeType.SYSTEM,
                999L,
                null,
                null,
                null,
                null
        );

        // when & then
        assertThatThrownBy(
                () -> operationalScopeCreationService.create(request)
        )
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(
                        OperationalScopeErrorCode.OPERATIONAL_SCOPE_PARENT_NOT_FOUND
                );

        verify(operationalScopeRepository, never())
                .nextScopeCodeSequenceValue();
        verify(operationalScopeRepository, never()).save(any());
    }

    @Test
    void createThrowsExceptionWhenOwnerDoesNotExist() {
        // given
        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        OperationalScopeCreateRequest request = createRequest(
                "Operations System",
                OperationalScopeType.SYSTEM,
                null,
                999L,
                null,
                null,
                null
        );

        // when & then
        assertThatThrownBy(
                () -> operationalScopeCreationService.create(request)
        )
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(
                        OperationalScopeErrorCode.OPERATIONAL_SCOPE_OWNER_NOT_FOUND
                );

        verify(operationalScopeRepository, never())
                .nextScopeCodeSequenceValue();
        verify(operationalScopeRepository, never()).save(any());
    }

    @Test
    void createThrowsExceptionWhenOwnerIsDisabled() {
        // given
        User disabledOwner = User.builder()
                .id(11L)
                .username("disabled")
                .name("Disabled Manager")
                .email("disabled@example.com")
                .enabled(false)
                .build();

        when(userRepository.findById(11L))
                .thenReturn(Optional.of(disabledOwner));

        OperationalScopeCreateRequest request = createRequest(
                "Operations System",
                OperationalScopeType.SYSTEM,
                null,
                11L,
                null,
                null,
                null
        );

        // when & then
        assertThatThrownBy(
                () -> operationalScopeCreationService.create(request)
        )
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(
                        OperationalScopeErrorCode.OPERATIONAL_SCOPE_OWNER_DISABLED
                );

        verify(operationalScopeRepository, never())
                .nextScopeCodeSequenceValue();
        verify(operationalScopeRepository, never()).save(any());
    }

    @Test
    void createThrowsExceptionWhenEffectivePeriodIsInvalid() {
        // given
        LocalDate effectiveFrom = LocalDate.of(2026, 12, 31);
        LocalDate effectiveTo = LocalDate.of(2026, 1, 1);

        OperationalScopeCreateRequest request = createRequest(
                "Operations System",
                OperationalScopeType.SYSTEM,
                null,
                null,
                null,
                effectiveFrom,
                effectiveTo
        );

        // when & then
        assertThatThrownBy(
                () -> operationalScopeCreationService.create(request)
        )
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(
                        OperationalScopeErrorCode.OPERATIONAL_SCOPE_EFFECTIVE_PERIOD_INVALID
                );

        verify(operationalScopeRepository, never())
                .nextScopeCodeSequenceValue();
        verify(operationalScopeRepository, never()).save(any());
    }

    private OperationalScopeCreateRequest createRequest(
            String scopeName,
            OperationalScopeType scopeType,
            Long parentScopeId,
            Long ownerId,
            String description,
            LocalDate effectiveFrom,
            LocalDate effectiveTo
    ) {
        OperationalScopeCreateRequest request =
                new OperationalScopeCreateRequest();

        ReflectionTestUtils.setField(request, "scopeName", scopeName);
        ReflectionTestUtils.setField(request, "scopeType", scopeType);
        ReflectionTestUtils.setField(request, "parentScopeId", parentScopeId);
        ReflectionTestUtils.setField(request, "ownerId", ownerId);
        ReflectionTestUtils.setField(request, "description", description);
        ReflectionTestUtils.setField(request, "effectiveFrom", effectiveFrom);
        ReflectionTestUtils.setField(request, "effectiveTo", effectiveTo);

        return request;
    }
}