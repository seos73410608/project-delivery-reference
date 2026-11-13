
package com.seos.cmdb.ci.service;

import com.seos.cmdb.ci.entity.CiType;
import com.seos.cmdb.ci.exception.ConfigurationItemErrorCode;
import com.seos.cmdb.ci.exception.CiTypeErrorCode;
import com.seos.cmdb.ci.repository.CiTypeRepository;
import com.seos.common.exception.BusinessException;
import com.seos.pmis.user.entity.User;
import com.seos.pmis.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfigurationItemValidationServiceTest {

    @Mock
    private CiTypeRepository ciTypeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ConfigurationItemValidationService validationService;

    private CiType ciType;
    private User manager;

    @BeforeEach
    void setUp() {
        ciType = CiType.builder()
                .id(1L)
                .code("SERVER")
                .name("서버")
                .assignable(true)
                .enabled(true)
                .build();

        manager = User.builder()
                .id(10L)
                .username("manager")
                .name("담당자")
                .email("manager@example.com")
                .enabled(true)
                .build();
    }

    @Test
    void resolveAssignableCiType_활성화되고_할당가능한_유형을_반환한다() {
        when(ciTypeRepository.findById(1L))
                .thenReturn(Optional.of(ciType));

        CiType result = validationService.resolveAssignableCiType(1L);

        assertThat(result).isSameAs(ciType);
        verify(ciTypeRepository).findById(1L);
    }

    @Test
    void resolveAssignableCiType_존재하지_않는_유형이면_예외가_발생한다() {
        when(ciTypeRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> validationService.resolveAssignableCiType(999L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(CiTypeErrorCode.CI_TYPE_NOT_FOUND);
    }

    @Test
    void resolveAssignableCiType_비활성화된_유형이면_예외가_발생한다() {
        CiType disabledType = CiType.builder()
                .id(2L)
                .code("SERVER")
                .name("서버")
                .assignable(true)
                .enabled(false)
                .build();

        when(ciTypeRepository.findById(2L))
                .thenReturn(Optional.of(disabledType));

        assertThatThrownBy(() -> validationService.resolveAssignableCiType(2L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(CiTypeErrorCode.CI_TYPE_DISABLED);
    }

    @Test
    void resolveAssignableCiType_할당할_수_없는_유형이면_예외가_발생한다() {
        CiType nonAssignableType = CiType.builder()
                .id(3L)
                .code("HARDWARE")
                .name("하드웨어")
                .assignable(false)
                .enabled(true)
                .build();

        when(ciTypeRepository.findById(3L))
                .thenReturn(Optional.of(nonAssignableType));

        assertThatThrownBy(() -> validationService.resolveAssignableCiType(3L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ConfigurationItemErrorCode.CI_TYPE_NOT_ASSIGNABLE);
    }

    @Test
    void resolveManager_활성화된_담당자를_반환한다() {
        when(userRepository.findById(10L))
                .thenReturn(Optional.of(manager));

        User result = validationService.resolveManager(10L);

        assertThat(result).isSameAs(manager);
        verify(userRepository).findById(10L);
    }

    @Test
    void resolveManager_담당자가_null이면_null을_반환한다() {
        User result = validationService.resolveManager(null);

        assertThat(result).isNull();
        verify(userRepository, never()).findById(any());
    }

    @Test
    void resolveManager_존재하지_않는_담당자이면_예외가_발생한다() {
        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> validationService.resolveManager(999L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ConfigurationItemErrorCode.CI_MANAGER_NOT_FOUND);
    }

    @Test
    void resolveManager_비활성화된_담당자이면_예외가_발생한다() {
        User disabledManager = User.builder()
                .id(11L)
                .username("disabled")
                .name("비활성 담당자")
                .email("disabled@example.com")
                .enabled(false)
                .build();

        when(userRepository.findById(11L))
                .thenReturn(Optional.of(disabledManager));

        assertThatThrownBy(() -> validationService.resolveManager(11L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ConfigurationItemErrorCode.CI_MANAGER_DISABLED);
    }
}
