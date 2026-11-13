
package com.seos.cmdb.ci.service;

import com.seos.cmdb.ci.dto.request.ChangeConfigurationItemStatusRequest;
import com.seos.cmdb.ci.dto.request.CreateConfigurationItemRequest;
import com.seos.cmdb.ci.dto.response.ConfigurationItemResponse;
import com.seos.cmdb.ci.entity.ConfigurationItem;
import com.seos.cmdb.ci.entity.CiEnvironment;
import com.seos.cmdb.ci.entity.CiStatus;
import com.seos.cmdb.ci.entity.CiType;
import com.seos.cmdb.ci.exception.ConfigurationItemErrorCode;
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
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfigurationItemServiceTest {

    @Mock
    private ConfigurationItemRepository configurationItemRepository;

    @Mock
    private ConfigurationItemMapper configurationItemMapper;

    @Mock
    private ConfigurationItemCreationService configurationItemCreationService;

    @Mock
    private ConfigurationItemValidationService configurationItemValidationService;

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
    void create_정상적으로_CI_생성을_위임한다() {
        // given
        CreateConfigurationItemRequest request =
                mock(CreateConfigurationItemRequest.class);

        when(request.getCiName()).thenReturn("운영 서버");
        when(request.getCiTypeId()).thenReturn(1L);
        when(request.getEnvironment()).thenReturn(CiEnvironment.PROD);
        when(request.getManagerId()).thenReturn(10L);
        when(request.getLocation()).thenReturn("서울 전산센터");
        when(request.getDescription()).thenReturn("운영 서버");

        ConfigurationItemResponse response =
                ConfigurationItemResponse.builder()
                        .id(100L)
                        .ciCode("CI-000001")
                        .ciName("운영 서버")
                        .ciTypeId(1L)
                        .status(CiStatus.PLANNED)
                        .build();

        when(configurationItemCreationService.create(
                "운영 서버",
                1L,
                CiEnvironment.PROD,
                10L,
                "서울 전산센터",
                "운영 서버"
        )).thenReturn(configurationItem);

        when(configurationItemMapper.toResponse(configurationItem))
                .thenReturn(response);

        // when
        ConfigurationItemResponse result =
                configurationItemService.create(request);

        // then
        assertThat(result.getCiCode()).isEqualTo("CI-000001");
        assertThat(result.getStatus()).isEqualTo(CiStatus.PLANNED);

        verify(configurationItemCreationService).create(
                "운영 서버",
                1L,
                CiEnvironment.PROD,
                10L,
                "서울 전산센터",
                "운영 서버"
        );
        verify(configurationItemMapper).toResponse(configurationItem);
    }

    @Test
    void getAll_전체_CI를_조회한다() {
        // given
        when(configurationItemRepository.findAll(any(Sort.class)))
                .thenReturn(List.of(configurationItem));

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
