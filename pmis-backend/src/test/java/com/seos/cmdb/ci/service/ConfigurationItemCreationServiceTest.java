
package com.seos.cmdb.ci.service;

import com.seos.cmdb.ci.entity.ConfigurationItem;
import com.seos.cmdb.ci.entity.CiEnvironment;
import com.seos.cmdb.ci.entity.CiStatus;
import com.seos.cmdb.ci.entity.CiType;
import com.seos.cmdb.ci.exception.ConfigurationItemErrorCode;
import com.seos.cmdb.ci.exception.CiTypeErrorCode;
import com.seos.cmdb.ci.mapper.ConfigurationItemMapper;
import com.seos.cmdb.ci.repository.ConfigurationItemRepository;
import com.seos.common.exception.BusinessException;
import com.seos.pmis.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfigurationItemCreationServiceTest {

    @Mock
    private ConfigurationItemRepository configurationItemRepository;

    @Mock
    private ConfigurationItemMapper configurationItemMapper;

    @Mock
    private ConfigurationItemValidationService configurationItemValidationService;

    @InjectMocks
    private ConfigurationItemCreationService configurationItemCreationService;

    private CiType ciType;
    private User manager;
    private ConfigurationItem configurationItem;

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

        configurationItem = ConfigurationItem.builder()
                .id(100L)
                .ciCode("CI-000001")
                .ciName("운영 서버")
                .ciType(ciType)
                .status(CiStatus.PLANNED)
                .environment(CiEnvironment.PROD)
                .manager(manager)
                .build();
    }

    @Test
    void create_정상적으로_CI를_생성한다() {
        // given
        when(configurationItemValidationService.resolveAssignableCiType(1L))
                .thenReturn(ciType);
        when(configurationItemValidationService.resolveManager(10L))
                .thenReturn(manager);
        when(configurationItemRepository.nextCiCodeSequenceValue())
                .thenReturn(1L);
        when(configurationItemMapper.toEntity(
                "운영 서버",
                ciType,
                CiEnvironment.PROD,
                manager,
                "서울 전산센터",
                "운영 서버",
                "CI-000001"
        )).thenReturn(configurationItem);
        when(configurationItemRepository.save(configurationItem))
                .thenReturn(configurationItem);

        // when
        ConfigurationItem result = configurationItemCreationService.create(
                "운영 서버",
                1L,
                CiEnvironment.PROD,
                10L,
                "서울 전산센터",
                "운영 서버"
        );

        // then
        assertThat(result).isSameAs(configurationItem);
        assertThat(result.getCiCode()).isEqualTo("CI-000001");

        verify(configurationItemValidationService)
                .resolveAssignableCiType(1L);
        verify(configurationItemValidationService)
                .resolveManager(10L);
        verify(configurationItemRepository).save(configurationItem);
    }

    @Test
    void create_담당자를_지정하지_않아도_CI를_생성한다() {
        // given
        when(configurationItemValidationService.resolveAssignableCiType(1L))
                .thenReturn(ciType);
        when(configurationItemValidationService.resolveManager(null))
                .thenReturn(null);
        when(configurationItemRepository.nextCiCodeSequenceValue())
                .thenReturn(1L);
        when(configurationItemMapper.toEntity(
                "운영 서버",
                ciType,
                CiEnvironment.PROD,
                null,
                null,
                null,
                "CI-000001"
        )).thenReturn(configurationItem);
        when(configurationItemRepository.save(configurationItem))
                .thenReturn(configurationItem);

        // when
        ConfigurationItem result = configurationItemCreationService.create(
                "운영 서버",
                1L,
                CiEnvironment.PROD,
                null,
                null,
                null
        );

        // then
        assertThat(result).isSameAs(configurationItem);

        verify(configurationItemValidationService)
                .resolveManager(null);
        verify(configurationItemRepository).save(configurationItem);
    }

    @Test
    void create_존재하지_않는_CI_Type이면_예외가_발생한다() {
        // given
        when(configurationItemValidationService.resolveAssignableCiType(999L))
                .thenThrow(new BusinessException(CiTypeErrorCode.CI_TYPE_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> configurationItemCreationService.create(
                "운영 서버", 999L, CiEnvironment.PROD, null, null, null
        ))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(CiTypeErrorCode.CI_TYPE_NOT_FOUND);

        verify(configurationItemRepository, never()).save(any());
    }

    @Test
    void create_비활성화된_CI_Type이면_예외가_발생한다() {
        // given
        when(configurationItemValidationService.resolveAssignableCiType(2L))
                .thenThrow(new BusinessException(CiTypeErrorCode.CI_TYPE_DISABLED));

        // when & then
        assertThatThrownBy(() -> configurationItemCreationService.create(
                "운영 서버", 2L, CiEnvironment.PROD, null, null, null
        ))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(CiTypeErrorCode.CI_TYPE_DISABLED);

        verify(configurationItemRepository, never()).save(any());
    }

    @Test
    void create_할당할_수_없는_CI_Type이면_예외가_발생한다() {
        // given
        when(configurationItemValidationService.resolveAssignableCiType(3L))
                .thenThrow(new BusinessException(
                        ConfigurationItemErrorCode.CI_TYPE_NOT_ASSIGNABLE
                ));

        // when & then
        assertThatThrownBy(() -> configurationItemCreationService.create(
                "운영 서버", 3L, CiEnvironment.PROD, null, null, null
        ))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ConfigurationItemErrorCode.CI_TYPE_NOT_ASSIGNABLE);

        verify(configurationItemRepository, never()).save(any());
    }

    @Test
    void create_존재하지_않는_담당자이면_예외가_발생한다() {
        // given
        when(configurationItemValidationService.resolveAssignableCiType(1L))
                .thenReturn(ciType);
        when(configurationItemValidationService.resolveManager(999L))
                .thenThrow(new BusinessException(
                        ConfigurationItemErrorCode.CI_MANAGER_NOT_FOUND
                ));

        // when & then
        assertThatThrownBy(() -> configurationItemCreationService.create(
                "운영 서버", 1L, CiEnvironment.PROD, 999L, null, null
        ))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ConfigurationItemErrorCode.CI_MANAGER_NOT_FOUND);

        verify(configurationItemRepository, never()).save(any());
    }

    @Test
    void create_비활성화된_담당자이면_예외가_발생한다() {
        // given
        when(configurationItemValidationService.resolveAssignableCiType(1L))
                .thenReturn(ciType);
        when(configurationItemValidationService.resolveManager(11L))
                .thenThrow(new BusinessException(
                        ConfigurationItemErrorCode.CI_MANAGER_DISABLED
                ));

        // when & then
        assertThatThrownBy(() -> configurationItemCreationService.create(
                "운영 서버", 1L, CiEnvironment.PROD, 11L, null, null
        ))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ConfigurationItemErrorCode.CI_MANAGER_DISABLED);

        verify(configurationItemRepository, never()).save(any());
    }
}
