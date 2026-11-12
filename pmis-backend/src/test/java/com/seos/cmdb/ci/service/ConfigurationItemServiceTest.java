
package com.seos.cmdb.ci.service;

import com.seos.cmdb.ci.dto.request.ChangeConfigurationItemStatusRequest;
import com.seos.cmdb.ci.dto.request.CreateConfigurationItemRequest;
import com.seos.cmdb.ci.dto.response.ConfigurationItemResponse;
import com.seos.cmdb.ci.entity.ConfigurationItem;
import com.seos.cmdb.ci.entity.CiEnvironment;
import com.seos.cmdb.ci.entity.CiStatus;
import com.seos.cmdb.ci.entity.CiType;
import com.seos.cmdb.ci.exception.ConfigurationItemErrorCode;
import com.seos.cmdb.ci.exception.CiTypeErrorCode;
import com.seos.cmdb.ci.mapper.ConfigurationItemMapper;
import com.seos.cmdb.ci.repository.CiTypeRepository;
import com.seos.cmdb.ci.repository.ConfigurationItemRepository;
import com.seos.common.exception.BusinessException;
import com.seos.pmis.user.entity.User;
import com.seos.pmis.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfigurationItemServiceTest {

    @Mock
    private ConfigurationItemRepository configurationItemRepository;

    @Mock
    private CiTypeRepository ciTypeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ConfigurationItemMapper configurationItemMapper;

    @InjectMocks
    private ConfigurationItemService configurationItemService;

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
        CreateConfigurationItemRequest request =
                mock(CreateConfigurationItemRequest.class);

        ConfigurationItemResponse response =
                ConfigurationItemResponse.builder()
                        .id(100L)
                        .ciCode("CI-000001")
                        .ciName("운영 서버")
                        .ciTypeId(1L)
                        .status(CiStatus.PLANNED)
                        .build();

        when(request.getCiTypeId()).thenReturn(1L);
        when(request.getManagerId()).thenReturn(10L);
        when(ciTypeRepository.findById(1L))
                .thenReturn(Optional.of(ciType));
        when(userRepository.findById(10L))
                .thenReturn(Optional.of(manager));
        when(configurationItemRepository.nextCiCodeSequenceValue())
                .thenReturn(1L);
        when(configurationItemMapper.toEntity(
                request, ciType, manager, "CI-000001"
        )).thenReturn(configurationItem);
        when(configurationItemRepository.save(configurationItem))
                .thenReturn(configurationItem);
        when(configurationItemMapper.toResponse(configurationItem))
                .thenReturn(response);

        // when
        ConfigurationItemResponse result =
                configurationItemService.create(request);

        // then
        assertThat(result.getCiCode()).isEqualTo("CI-000001");
        assertThat(result.getStatus()).isEqualTo(CiStatus.PLANNED);

        verify(configurationItemRepository).save(configurationItem);
    }

    @Test
    void create_담당자를_지정하지_않아도_CI를_생성한다() {
        // given
        CreateConfigurationItemRequest request =
                mock(CreateConfigurationItemRequest.class);

        when(request.getCiTypeId()).thenReturn(1L);
        when(request.getManagerId()).thenReturn(null);
        when(ciTypeRepository.findById(1L))
                .thenReturn(Optional.of(ciType));
        when(configurationItemRepository.nextCiCodeSequenceValue())
                .thenReturn(1L);
        when(configurationItemMapper.toEntity(
                request, ciType, null, "CI-000001"
        )).thenReturn(configurationItem);
        when(configurationItemRepository.save(configurationItem))
                .thenReturn(configurationItem);
        when(configurationItemMapper.toResponse(configurationItem))
                .thenReturn(ConfigurationItemResponse.builder()
                        .ciCode("CI-000001")
                        .build());

        // when
        ConfigurationItemResponse result =
                configurationItemService.create(request);

        // then
        assertThat(result.getCiCode()).isEqualTo("CI-000001");

        verify(userRepository, never()).findById(any());
    }

    @Test
    void create_존재하지_않는_CI_Type이면_예외가_발생한다() {
        // given
        CreateConfigurationItemRequest request =
                mock(CreateConfigurationItemRequest.class);

        when(request.getCiTypeId()).thenReturn(999L);
        when(ciTypeRepository.findById(999L))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> configurationItemService.create(request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(CiTypeErrorCode.CI_TYPE_NOT_FOUND);

        verify(configurationItemRepository, never()).save(any());
    }

    @Test
    void create_비활성화된_CI_Type이면_예외가_발생한다() {
        // given
        CreateConfigurationItemRequest request =
                mock(CreateConfigurationItemRequest.class);

        CiType disabledType = CiType.builder()
                .id(2L)
                .code("SERVER")
                .name("서버")
                .assignable(true)
                .enabled(false)
                .build();

        when(request.getCiTypeId()).thenReturn(2L);
        when(ciTypeRepository.findById(2L))
                .thenReturn(Optional.of(disabledType));

        // when & then
        assertThatThrownBy(() -> configurationItemService.create(request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(CiTypeErrorCode.CI_TYPE_DISABLED);

        verify(configurationItemRepository, never()).save(any());
    }

    @Test
    void create_할당할_수_없는_CI_Type이면_예외가_발생한다() {
        // given
        CreateConfigurationItemRequest request =
                mock(CreateConfigurationItemRequest.class);

        CiType nonAssignableType = CiType.builder()
                .id(3L)
                .code("HARDWARE")
                .name("하드웨어")
                .assignable(false)
                .enabled(true)
                .build();

        when(request.getCiTypeId()).thenReturn(3L);
        when(ciTypeRepository.findById(3L))
                .thenReturn(Optional.of(nonAssignableType));

        // when & then
        assertThatThrownBy(() -> configurationItemService.create(request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(
                        ConfigurationItemErrorCode.CI_TYPE_NOT_ASSIGNABLE
                );

        verify(configurationItemRepository, never()).save(any());
    }

    @Test
    void create_존재하지_않는_담당자이면_예외가_발생한다() {
        // given
        CreateConfigurationItemRequest request =
                mock(CreateConfigurationItemRequest.class);

        when(request.getCiTypeId()).thenReturn(1L);
        when(request.getManagerId()).thenReturn(999L);
        when(ciTypeRepository.findById(1L))
                .thenReturn(Optional.of(ciType));
        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> configurationItemService.create(request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(
                        ConfigurationItemErrorCode.CI_MANAGER_NOT_FOUND
                );

        verify(configurationItemRepository, never()).save(any());
    }

    @Test
    void create_비활성화된_담당자이면_예외가_발생한다() {
        // given
        CreateConfigurationItemRequest request =
                mock(CreateConfigurationItemRequest.class);

        User disabledManager = User.builder()
                .id(11L)
                .enabled(false)
                .build();

        when(request.getCiTypeId()).thenReturn(1L);
        when(request.getManagerId()).thenReturn(11L);
        when(ciTypeRepository.findById(1L))
                .thenReturn(Optional.of(ciType));
        when(userRepository.findById(11L))
                .thenReturn(Optional.of(disabledManager));

        // when & then
        assertThatThrownBy(() -> configurationItemService.create(request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(
                        ConfigurationItemErrorCode.CI_MANAGER_DISABLED
                );

        verify(configurationItemRepository, never()).save(any());
    }

    @Test
    void getAll_전체_CI를_조회한다() {
        // given
        when(configurationItemRepository.findAll(
                org.mockito.ArgumentMatchers.any(
                        org.springframework.data.domain.Sort.class
                )
        )).thenReturn(List.of(configurationItem));

        when(configurationItemMapper.toResponse(configurationItem))
                .thenReturn(ConfigurationItemResponse.builder()
                        .id(100L)
                        .ciCode("CI-000001")
                        .build());

        // when
        List<ConfigurationItemResponse> result =
                configurationItemService.getAll();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCiCode()).isEqualTo("CI-000001");
    }

    @Test
    void getById_존재하지_않는_CI이면_예외가_발생한다() {
        // given
        when(configurationItemRepository.findById(999L))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> configurationItemService.getById(999L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ConfigurationItemErrorCode.CI_NOT_FOUND);
    }

    @Test
    void changeStatus_정상적으로_CI_상태를_변경한다() {
        // given
        ChangeConfigurationItemStatusRequest request =
                mock(ChangeConfigurationItemStatusRequest.class);

        when(request.getStatus()).thenReturn(CiStatus.ACTIVE);
        when(configurationItemRepository.findById(100L))
                .thenReturn(Optional.of(configurationItem));
        when(configurationItemMapper.toResponse(configurationItem))
                .thenReturn(ConfigurationItemResponse.builder()
                        .id(100L)
                        .status(CiStatus.ACTIVE)
                        .build());

        // when
        ConfigurationItemResponse result =
                configurationItemService.changeStatus(100L, request);

        // then
        assertThat(configurationItem.getStatus())
                .isEqualTo(CiStatus.ACTIVE);
        assertThat(result.getStatus()).isEqualTo(CiStatus.ACTIVE);
    }
}
