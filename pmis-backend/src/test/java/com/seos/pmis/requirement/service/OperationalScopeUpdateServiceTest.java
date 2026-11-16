
package com.seos.pmis.requirement.service;

import com.seos.common.exception.BusinessException;
import com.seos.pmis.requirement.dto.request.OperationalScopeUpdateRequest;
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
class OperationalScopeUpdateServiceTest {

    @Mock
    private OperationalScopeRepository operationalScopeRepository;

    @Mock
    private UserRepository userRepository;

    private OperationalScopeUpdateService operationalScopeUpdateService;

    private OperationalScope operationalScope;
    private OperationalScope parentScope;
    private User owner;

    @BeforeEach
    void setUp() {
        operationalScopeUpdateService =
                new OperationalScopeUpdateService(
                        operationalScopeRepository,
                        userRepository,
                        new OperationalScopeMapper()
                );

        operationalScope = OperationalScope.builder()
                .id(1L)
                .scopeCode("OPS-000001")
                .scopeName("Original Scope")
                .scopeType(OperationalScopeType.SYSTEM)
                .status(OperationalScopeStatus.PLANNED)
                .version(1L)
                .build();

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
    void update_updatesOperationalScopeSuccessfully() {
        // given
        when(operationalScopeRepository.findById(1L))
                .thenReturn(Optional.of(operationalScope));

        when(operationalScopeRepository.saveAndFlush(any(OperationalScope.class)))
                .thenAnswer(invocation -> {
                    OperationalScope scope = invocation.getArgument(0);
                    ReflectionTestUtils.setField(
                            scope,
                            "version",
                            scope.getVersion() + 1L
                    );
                    return scope;
                });

        OperationalScopeUpdateRequest request = createRequest(
                1L,
                "Updated Scope",
                OperationalScopeType.SERVICE,
                null,
                null,
                "Updated description",
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31)
        );

        // when
        OperationalScopeResponse result =
                operationalScopeUpdateService.update(1L, request);

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getScopeCode()).isEqualTo("OPS-000001");
        assertThat(result.getScopeName()).isEqualTo("Updated Scope");
        assertThat(result.getScopeType()).isEqualTo(OperationalScopeType.SERVICE);
        assertThat(result.getStatus()).isEqualTo(OperationalScopeStatus.PLANNED);
        assertThat(result.getDescription()).isEqualTo("Updated description");
        assertThat(result.getEffectiveFrom())
                .isEqualTo(LocalDate.of(2026, 1, 1));
        assertThat(result.getEffectiveTo())
                .isEqualTo(LocalDate.of(2026, 12, 31));
        assertThat(result.getVersion()).isEqualTo(2L);

        verify(operationalScopeRepository).findById(1L);
        verify(operationalScopeRepository).saveAndFlush(operationalScope);
        verify(userRepository, never()).findById(any());
    }

    @Test
    void update_updatesParentAndOwnerSuccessfully() {
        // given
        when(operationalScopeRepository.findById(1L))
                .thenReturn(Optional.of(operationalScope));

        when(operationalScopeRepository.findById(20L))
                .thenReturn(Optional.of(parentScope));

        when(userRepository.findById(10L))
                .thenReturn(Optional.of(owner));

        when(operationalScopeRepository.saveAndFlush(any(OperationalScope.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OperationalScopeUpdateRequest request = createRequest(
                1L,
                "Updated Scope",
                OperationalScopeType.DATABASE,
                20L,
                10L,
                "Database scope",
                null,
                null
        );

        // when
        OperationalScopeResponse result =
                operationalScopeUpdateService.update(1L, request);

        // then
        assertThat(result.getParentScopeId()).isEqualTo(20L);
        assertThat(result.getParentScopeCode()).isEqualTo("OPS-000020");
        assertThat(result.getParentScopeName()).isEqualTo("Operations Service");
        assertThat(result.getOwnerId()).isEqualTo(10L);
        assertThat(result.getOwnerUsername()).isEqualTo("manager");
        assertThat(result.getOwnerName()).isEqualTo("Manager");

        verify(operationalScopeRepository).findById(20L);
        verify(userRepository).findById(10L);
        verify(operationalScopeRepository).saveAndFlush(operationalScope);
    }

    @Test
    void updateThrowsExceptionWhenScopeDoesNotExist() {
        // given
        when(operationalScopeRepository.findById(999L))
                .thenReturn(Optional.empty());

        OperationalScopeUpdateRequest request = createRequest(
                1L,
                "Updated Scope",
                OperationalScopeType.SYSTEM,
                null,
                null,
                null,
                null,
                null
        );

        // when & then
        assertThatThrownBy(
                () -> operationalScopeUpdateService.update(999L, request)
        )
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(
                        OperationalScopeErrorCode.OPERATIONAL_SCOPE_NOT_FOUND
                );

        verify(operationalScopeRepository, never()).saveAndFlush(any());
    }

    @Test
    void updateThrowsExceptionWhenVersionIsDifferent() {
        // given
        when(operationalScopeRepository.findById(1L))
                .thenReturn(Optional.of(operationalScope));

        OperationalScopeUpdateRequest request = createRequest(
                0L,
                "Updated Scope",
                OperationalScopeType.SYSTEM,
                null,
                null,
                null,
                null,
                null
        );

        // when & then
        assertThatThrownBy(
                () -> operationalScopeUpdateService.update(1L, request)
        )
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(
                        OperationalScopeErrorCode.OPERATIONAL_SCOPE_VERSION_CONFLICT
                );

        verify(operationalScopeRepository, never()).saveAndFlush(any());
    }

    @Test
    void updateThrowsExceptionWhenParentScopeDoesNotExist() {
        // given
        when(operationalScopeRepository.findById(1L))
                .thenReturn(Optional.of(operationalScope));

        when(operationalScopeRepository.findById(999L))
                .thenReturn(Optional.empty());

        OperationalScopeUpdateRequest request = createRequest(
                1L,
                "Updated Scope",
                OperationalScopeType.SYSTEM,
                999L,
                null,
                null,
                null,
                null
        );

        // when & then
        assertThatThrownBy(
                () -> operationalScopeUpdateService.update(1L, request)
        )
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(
                        OperationalScopeErrorCode.OPERATIONAL_SCOPE_PARENT_NOT_FOUND
                );

        verify(operationalScopeRepository, never()).saveAndFlush(any());
    }

    @Test
    void updateThrowsExceptionWhenScopeIsAssignedAsItsOwnParent() {
        // given
        when(operationalScopeRepository.findById(1L))
                .thenReturn(Optional.of(operationalScope));

        OperationalScopeUpdateRequest request = createRequest(
                1L,
                "Updated Scope",
                OperationalScopeType.SYSTEM,
                1L,
                null,
                null,
                null,
                null
        );

        // when & then
        assertThatThrownBy(
                () -> operationalScopeUpdateService.update(1L, request)
        )
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(
                        OperationalScopeErrorCode.OPERATIONAL_SCOPE_PARENT_INVALID
                );

        verify(operationalScopeRepository, never()).saveAndFlush(any());
    }

    @Test
    void updateThrowsExceptionWhenOwnerDoesNotExist() {
        // given
        when(operationalScopeRepository.findById(1L))
                .thenReturn(Optional.of(operationalScope));

        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        OperationalScopeUpdateRequest request = createRequest(
                1L,
                "Updated Scope",
                OperationalScopeType.SYSTEM,
                null,
                999L,
                null,
                null,
                null
        );

        // when & then
        assertThatThrownBy(
                () -> operationalScopeUpdateService.update(1L, request)
        )
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(
                        OperationalScopeErrorCode.OPERATIONAL_SCOPE_OWNER_NOT_FOUND
                );

        verify(operationalScopeRepository, never()).saveAndFlush(any());
    }

    @Test
    void updateThrowsExceptionWhenOwnerIsDisabled() {
        // given
        User disabledOwner = User.builder()
                .id(11L)
                .username("disabled")
                .name("Disabled Manager")
                .email("disabled@example.com")
                .enabled(false)
                .build();

        when(operationalScopeRepository.findById(1L))
                .thenReturn(Optional.of(operationalScope));

        when(userRepository.findById(11L))
                .thenReturn(Optional.of(disabledOwner));

        OperationalScopeUpdateRequest request = createRequest(
                1L,
                "Updated Scope",
                OperationalScopeType.SYSTEM,
                null,
                11L,
                null,
                null,
                null
        );

        // when & then
        assertThatThrownBy(
                () -> operationalScopeUpdateService.update(1L, request)
        )
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(
                        OperationalScopeErrorCode.OPERATIONAL_SCOPE_OWNER_DISABLED
                );

        verify(operationalScopeRepository, never()).saveAndFlush(any());
    }

    @Test
    void updateThrowsExceptionWhenEffectivePeriodIsInvalid() {
        // given
        OperationalScopeUpdateRequest request = createRequest(
                1L,
                "Updated Scope",
                OperationalScopeType.SYSTEM,
                null,
                null,
                null,
                LocalDate.of(2026, 12, 31),
                LocalDate.of(2026, 1, 1)
        );

        when(operationalScopeRepository.findById(1L))
                .thenReturn(Optional.of(operationalScope));

        // when & then
        assertThatThrownBy(
                () -> operationalScopeUpdateService.update(1L, request)
        )
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(
                        OperationalScopeErrorCode.OPERATIONAL_SCOPE_EFFECTIVE_PERIOD_INVALID
                );

        verify(operationalScopeRepository, never()).saveAndFlush(any());
    }

    private OperationalScopeUpdateRequest createRequest(
            Long version,
            String scopeName,
            OperationalScopeType scopeType,
            Long parentScopeId,
            Long ownerId,
            String description,
            LocalDate effectiveFrom,
            LocalDate effectiveTo
    ) {
        OperationalScopeUpdateRequest request =
                new OperationalScopeUpdateRequest();

        ReflectionTestUtils.setField(request, "version", version);
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
