
package com.seos.cmdb.server.service;

import com.seos.cmdb.ci.dto.response.ConfigurationItemResponse;
import com.seos.cmdb.ci.entity.CiEnvironment;
import com.seos.cmdb.ci.entity.CiStatus;
import com.seos.cmdb.ci.entity.CiType;
import com.seos.cmdb.ci.entity.ConfigurationItem;
import com.seos.cmdb.ci.mapper.ConfigurationItemMapper;
import com.seos.cmdb.ci.service.ConfigurationItemCreationService;
import com.seos.cmdb.ci.service.ConfigurationItemValidationService;
import com.seos.cmdb.server.dto.request.CreateServerRequest;
import com.seos.cmdb.server.dto.response.ServerRegistrationResponse;
import com.seos.cmdb.server.entity.ServerCategory;
import com.seos.cmdb.server.exception.ServerErrorCode;
import com.seos.cmdb.server.repository.ServerDetailRepository;
import com.seos.common.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServerRegistrationServiceTest {

    @Mock
    private ConfigurationItemCreationService configurationItemCreationService;

    @Mock
    private ConfigurationItemValidationService configurationItemValidationService;

    @Mock
    private ConfigurationItemMapper configurationItemMapper;

    @Mock
    private ServerDetailRepository serverDetailRepository;

    @InjectMocks
    private ServerRegistrationService serverRegistrationService;

    private CiType serverCiType;
    private ConfigurationItem configurationItem;
    private ConfigurationItemResponse ciResponse;
    private CreateServerRequest request;

    @BeforeEach
    void setUp() {

        serverCiType = CiType.builder()
                .id(1L)
                .code("SERVER")
                .name("서버")
                .assignable(true)
                .enabled(true)
                .build();

        configurationItem = ConfigurationItem.builder()
                .id(100L)
                .ciCode("CI-000101")
                .ciName("운영 WEB 서버 01")
                .ciType(serverCiType)
                .status(CiStatus.PLANNED)
                .environment(CiEnvironment.PROD)
                .build();

        ciResponse = ConfigurationItemResponse.builder()
                .id(100L)
                .ciCode("CI-000101")
                .ciName("운영 WEB 서버 01")
                .ciTypeId(1L)
                .ciTypeCode("SERVER")
                .ciTypeName("서버")
                .status(CiStatus.PLANNED)
                .environment(CiEnvironment.PROD)
                .build();

        request = new CreateServerRequest();

        ReflectionTestUtils.setField(request, "ciName", "운영 WEB 서버 01");
        ReflectionTestUtils.setField(request, "ciTypeId", 1L);
        ReflectionTestUtils.setField(request, "hostname", "web-prod-01");
        ReflectionTestUtils.setField(request, "serverCategory", ServerCategory.PHYSICAL);
        ReflectionTestUtils.setField(request, "environment", CiEnvironment.PROD);
        ReflectionTestUtils.setField(request, "managementIp", "192.168.10.11");
        ReflectionTestUtils.setField(request, "osName", "AIX");
        ReflectionTestUtils.setField(request, "osVersion", "7.2");
        ReflectionTestUtils.setField(request, "cpuCores", 8);
        ReflectionTestUtils.setField(request, "memorySizeMb", 32768L);
    }

    @Test
    void register_SERVER_CI와_서버상세정보를_함께_생성한다() {
        // given
        when(configurationItemValidationService.resolveAssignableCiType(1L))
                .thenReturn(serverCiType);

        when(configurationItemValidationService.resolveManager(null))
                .thenReturn(null);

        when(configurationItemCreationService.create(
                "운영 WEB 서버 01",
                serverCiType,
                CiEnvironment.PROD,
                null,
                null,
                null
        )).thenReturn(configurationItem);

        when(configurationItemMapper.toResponse(configurationItem))
                .thenReturn(ciResponse);

        // when
        ServerRegistrationResponse result =
                serverRegistrationService.register(request);

        // then
        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getCiCode()).isEqualTo("CI-000101");
        assertThat(result.getCiTypeCode()).isEqualTo("SERVER");

        assertThat(result.getServerDetail()).isNotNull();
        assertThat(result.getServerDetail().getHostname())
                .isEqualTo("web-prod-01");
        assertThat(result.getServerDetail().getServerCategory())
                .isEqualTo(ServerCategory.PHYSICAL);
        assertThat(result.getServerDetail().getManagementIp())
                .isEqualTo("192.168.10.11");
        assertThat(result.getServerDetail().getOsName())
                .isEqualTo("AIX");
        assertThat(result.getServerDetail().getCpuCores())
                .isEqualTo(8);
        assertThat(result.getServerDetail().getMemorySizeMb())
                .isEqualTo(32768L);

        verify(configurationItemCreationService).create(
                "운영 WEB 서버 01",
                serverCiType,
                CiEnvironment.PROD,
                null,
                null,
                null
        );

        verify(serverDetailRepository).save(any());
    }

    @Test
    void register_SERVER가_아닌_CI_Type이면_예외가_발생한다() {
        // given
        CiType otherCiType = CiType.builder()
                .id(2L)
                .code("DATABASE")
                .name("데이터베이스")
                .assignable(true)
                .enabled(true)
                .build();

        when(configurationItemValidationService.resolveAssignableCiType(1L))
                .thenReturn(otherCiType);

        // when & then
        assertThatThrownBy(() -> serverRegistrationService.register(request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ServerErrorCode.SERVER_CI_TYPE_REQUIRED);

        verify(configurationItemCreationService, never())
                .create(
                        anyString(),
                        any(CiType.class),
                        any(),
                        any(),
                        any(),
                        any()
                );

        verify(serverDetailRepository, never()).save(any());
    }

    @Test
    void register_담당자_검증에_실패하면_CI를_생성하지_않는다() {
        // given
        when(configurationItemValidationService.resolveAssignableCiType(1L))
                .thenReturn(serverCiType);

        when(configurationItemValidationService.resolveManager(null))
                .thenThrow(new BusinessException(
                        com.seos.cmdb.ci.exception.ConfigurationItemErrorCode
                                .CI_MANAGER_NOT_FOUND
                ));

        // when & then
        assertThatThrownBy(() -> serverRegistrationService.register(request))
                .isInstanceOf(BusinessException.class);

        verify(configurationItemCreationService, never())
                .create(
                        anyString(),
                        any(CiType.class),
                        any(),
                        any(),
                        any(),
                        any()
                );

        verify(serverDetailRepository, never()).save(any());
    }

    @Test
    void register_서버상세정보_저장에_실패하면_예외를_전파한다() {
        // given
        when(configurationItemValidationService.resolveAssignableCiType(1L))
                .thenReturn(serverCiType);

        when(configurationItemValidationService.resolveManager(null))
                .thenReturn(null);

        when(configurationItemCreationService.create(
                "운영 WEB 서버 01",
                serverCiType,
                CiEnvironment.PROD,
                null,
                null,
                null
        )).thenReturn(configurationItem);

        doThrow(new RuntimeException("서버 상세정보 저장 실패"))
                .when(serverDetailRepository)
                .save(any());

        // when & then
        assertThatThrownBy(() -> serverRegistrationService.register(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("서버 상세정보 저장 실패");

        verify(configurationItemCreationService).create(
                "운영 WEB 서버 01",
                serverCiType,
                CiEnvironment.PROD,
                null,
                null,
                null
        );
    }
}
