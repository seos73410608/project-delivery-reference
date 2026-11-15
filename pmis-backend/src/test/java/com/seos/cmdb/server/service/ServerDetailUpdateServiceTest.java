
package com.seos.cmdb.server.service;

import com.seos.cmdb.ci.dto.response.ConfigurationItemResponse;
import com.seos.cmdb.ci.entity.CiType;
import com.seos.cmdb.ci.entity.ConfigurationItem;
import com.seos.cmdb.ci.exception.ConfigurationItemErrorCode;
import com.seos.cmdb.ci.mapper.ConfigurationItemMapper;
import com.seos.cmdb.ci.repository.ConfigurationItemRepository;
import com.seos.cmdb.server.dto.request.UpdateServerDetailRequest;
import com.seos.cmdb.server.dto.response.ServerRegistrationResponse;
import com.seos.cmdb.server.entity.ServerCategory;
import com.seos.cmdb.server.entity.ServerDetail;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServerDetailUpdateServiceTest {

    @Mock
    private ConfigurationItemRepository configurationItemRepository;

    @Mock
    private ServerDetailRepository serverDetailRepository;

    @Mock
    private ConfigurationItemMapper configurationItemMapper;

    @InjectMocks
    private ServerDetailUpdateService serverDetailUpdateService;

    private ConfigurationItem configurationItem;
    private ConfigurationItemResponse ciResponse;
    private ServerDetail serverDetail;

    @BeforeEach
    void setUp() {

        CiType serverCiType = CiType.builder()
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
                .build();

        ciResponse = ConfigurationItemResponse.builder()
                .id(100L)
                .ciCode("CI-000101")
                .ciName("운영 WEB 서버 01")
                .ciTypeId(1L)
                .ciTypeCode("SERVER")
                .ciTypeName("서버")
                .build();

        serverDetail = ServerDetail.builder()
                .configurationItem(configurationItem)
                .hostname("web-prod-01")
                .serverCategory(ServerCategory.PHYSICAL)
                .managementIp("192.168.10.11")
                .osName("AIX")
                .osVersion("7.2")
                .cpuCores(8)
                .memorySizeMb(32768L)
                .virtualizationPlatform("PowerVM")
                .build();
    }

    @Test
    void updateServerDetail_서버상세정보를_전체_수정한다() {
        // given
        UpdateServerDetailRequest request = createRequest(
                "web-prod-02",
                ServerCategory.VIRTUAL,
                "192.168.10.12",
                "Linux",
                "RHEL 9.6",
                16,
                65536L,
                "VMware"
        );

        when(configurationItemRepository.findById(100L))
                .thenReturn(Optional.of(configurationItem));

        when(serverDetailRepository.findByConfigurationItemId(100L))
                .thenReturn(Optional.of(serverDetail));

        when(configurationItemMapper.toResponse(configurationItem))
                .thenReturn(ciResponse);

        // when
        ServerRegistrationResponse result =
                serverDetailUpdateService.updateServerDetail(100L, request);

        // then
        assertThat(serverDetail.getHostname()).isEqualTo("web-prod-02");
        assertThat(serverDetail.getServerCategory())
                .isEqualTo(ServerCategory.VIRTUAL);
        assertThat(serverDetail.getManagementIp())
                .isEqualTo("192.168.10.12");
        assertThat(serverDetail.getOsName()).isEqualTo("Linux");
        assertThat(serverDetail.getOsVersion()).isEqualTo("RHEL 9.6");
        assertThat(serverDetail.getCpuCores()).isEqualTo(16);
        assertThat(serverDetail.getMemorySizeMb()).isEqualTo(65536L);
        assertThat(serverDetail.getVirtualizationPlatform())
                .isEqualTo("VMware");

        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getServerDetail().getHostname())
                .isEqualTo("web-prod-02");
        assertThat(result.getServerDetail().getServerCategory())
                .isEqualTo(ServerCategory.VIRTUAL);

        verify(configurationItemRepository).findById(100L);
        verify(serverDetailRepository).findByConfigurationItemId(100L);
        verify(configurationItemMapper).toResponse(configurationItem);
    }

    @Test
    void updateServerDetail_선택필드가_null이면_기존값을_초기화한다() {
        // given
        UpdateServerDetailRequest request = createRequest(
                "web-prod-02",
                ServerCategory.PHYSICAL,
                null,
                null,
                null,
                null,
                null,
                null
        );

        when(configurationItemRepository.findById(100L))
                .thenReturn(Optional.of(configurationItem));

        when(serverDetailRepository.findByConfigurationItemId(100L))
                .thenReturn(Optional.of(serverDetail));

        when(configurationItemMapper.toResponse(configurationItem))
                .thenReturn(ciResponse);

        // when
        ServerRegistrationResponse result =
                serverDetailUpdateService.updateServerDetail(100L, request);

        // then
        assertThat(serverDetail.getHostname()).isEqualTo("web-prod-02");
        assertThat(serverDetail.getManagementIp()).isNull();
        assertThat(serverDetail.getOsName()).isNull();
        assertThat(serverDetail.getOsVersion()).isNull();
        assertThat(serverDetail.getCpuCores()).isNull();
        assertThat(serverDetail.getMemorySizeMb()).isNull();
        assertThat(serverDetail.getVirtualizationPlatform()).isNull();

        assertThat(result.getServerDetail().getManagementIp()).isNull();
        assertThat(result.getServerDetail().getOsName()).isNull();
    }

    @Test
    void updateServerDetail_존재하지_않는_CI이면_예외가_발생한다() {
        // given
        UpdateServerDetailRequest request = createRequest(
                "web-prod-02",
                ServerCategory.PHYSICAL,
                null, null, null, null, null, null
        );

        when(configurationItemRepository.findById(999L))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                serverDetailUpdateService.updateServerDetail(999L, request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ConfigurationItemErrorCode.CI_NOT_FOUND);

        verify(serverDetailRepository, never())
                .findByConfigurationItemId(anyLong());

        verify(configurationItemMapper, never())
                .toResponse(any());
    }

    @Test
    void updateServerDetail_SERVER가_아닌_CI이면_예외가_발생한다() {
        // given
        UpdateServerDetailRequest request = createRequest(
                "db-prod-01",
                ServerCategory.PHYSICAL,
                null, null, null, null, null, null
        );

        CiType databaseCiType = CiType.builder()
                .id(2L)
                .code("DATABASE")
                .name("데이터베이스")
                .assignable(true)
                .enabled(true)
                .build();

        ConfigurationItem databaseCi = ConfigurationItem.builder()
                .id(200L)
                .ciCode("CI-000201")
                .ciName("운영 DB")
                .ciType(databaseCiType)
                .build();

        when(configurationItemRepository.findById(200L))
                .thenReturn(Optional.of(databaseCi));

        // when & then
        assertThatThrownBy(() ->
                serverDetailUpdateService.updateServerDetail(200L, request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ServerErrorCode.SERVER_CI_TYPE_REQUIRED);

        verify(serverDetailRepository, never())
                .findByConfigurationItemId(anyLong());

        verify(configurationItemMapper, never())
                .toResponse(any());
    }

    @Test
    void updateServerDetail_서버상세정보가_없으면_예외가_발생한다() {
        // given
        UpdateServerDetailRequest request = createRequest(
                "web-prod-02",
                ServerCategory.PHYSICAL,
                null, null, null, null, null, null
        );

        when(configurationItemRepository.findById(100L))
                .thenReturn(Optional.of(configurationItem));

        when(serverDetailRepository.findByConfigurationItemId(100L))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                serverDetailUpdateService.updateServerDetail(100L, request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ServerErrorCode.SERVER_DETAIL_NOT_FOUND);

        verify(configurationItemMapper, never())
                .toResponse(any());
    }

    private UpdateServerDetailRequest createRequest(
            String hostname,
            ServerCategory serverCategory,
            String managementIp,
            String osName,
            String osVersion,
            Integer cpuCores,
            Long memorySizeMb,
            String virtualizationPlatform
    ) {
        UpdateServerDetailRequest request = new UpdateServerDetailRequest();

        ReflectionTestUtils.setField(request, "hostname", hostname);
        ReflectionTestUtils.setField(request, "serverCategory", serverCategory);
        ReflectionTestUtils.setField(request, "managementIp", managementIp);
        ReflectionTestUtils.setField(request, "osName", osName);
        ReflectionTestUtils.setField(request, "osVersion", osVersion);
        ReflectionTestUtils.setField(request, "cpuCores", cpuCores);
        ReflectionTestUtils.setField(request, "memorySizeMb", memorySizeMb);
        ReflectionTestUtils.setField(
                request,
                "virtualizationPlatform",
                virtualizationPlatform
        );

        return request;
    }
}
