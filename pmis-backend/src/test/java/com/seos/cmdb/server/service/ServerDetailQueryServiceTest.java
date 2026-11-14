
package com.seos.cmdb.server.service;

import com.seos.cmdb.ci.dto.response.ConfigurationItemResponse;
import com.seos.cmdb.ci.entity.CiEnvironment;
import com.seos.cmdb.ci.entity.CiStatus;
import com.seos.cmdb.ci.entity.CiType;
import com.seos.cmdb.ci.entity.ConfigurationItem;
import com.seos.cmdb.ci.exception.ConfigurationItemErrorCode;
import com.seos.cmdb.ci.mapper.ConfigurationItemMapper;
import com.seos.cmdb.ci.repository.ConfigurationItemRepository;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServerDetailQueryServiceTest {

    @Mock
    private ConfigurationItemRepository configurationItemRepository;

    @Mock
    private ServerDetailRepository serverDetailRepository;

    @Mock
    private ConfigurationItemMapper configurationItemMapper;

    @InjectMocks
    private ServerDetailQueryService serverDetailQueryService;

    private CiType serverCiType;
    private ConfigurationItem configurationItem;
    private ConfigurationItemResponse ciResponse;
    private ServerDetail serverDetail;

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

        serverDetail = ServerDetail.builder()
                .configurationItem(configurationItem)
                .hostname("web-prod-01")
                .serverCategory(ServerCategory.PHYSICAL)
                .managementIp("192.168.10.11")
                .osName("AIX")
                .osVersion("7.2")
                .cpuCores(8)
                .memorySizeMb(32768L)
                .build();
    }

    @Test
    void getServerDetail_SERVER_CI와_서버상세정보를_조회한다() {
        // given
        when(configurationItemRepository.findById(100L))
                .thenReturn(Optional.of(configurationItem));

        when(serverDetailRepository.findByConfigurationItemId(100L))
                .thenReturn(Optional.of(serverDetail));

        when(configurationItemMapper.toResponse(configurationItem))
                .thenReturn(ciResponse);

        // when
        ServerRegistrationResponse result =
                serverDetailQueryService.getServerDetail(100L);

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
        assertThat(result.getServerDetail().getOsVersion())
                .isEqualTo("7.2");
        assertThat(result.getServerDetail().getCpuCores())
                .isEqualTo(8);
        assertThat(result.getServerDetail().getMemorySizeMb())
                .isEqualTo(32768L);

        verify(configurationItemRepository).findById(100L);
        verify(serverDetailRepository).findByConfigurationItemId(100L);
        verify(configurationItemMapper).toResponse(configurationItem);
    }

    @Test
    void getServerDetail_존재하지_않는_CI이면_예외가_발생한다() {
        // given
        when(configurationItemRepository.findById(999L))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                serverDetailQueryService.getServerDetail(999L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ConfigurationItemErrorCode.CI_NOT_FOUND);

        verify(serverDetailRepository, never())
                .findByConfigurationItemId(anyLong());

        verify(configurationItemMapper, never())
                .toResponse(any());
    }

    @Test
    void getServerDetail_SERVER가_아닌_CI이면_예외가_발생한다() {
        // given
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
                serverDetailQueryService.getServerDetail(200L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ServerErrorCode.SERVER_CI_TYPE_REQUIRED);

        verify(serverDetailRepository, never())
                .findByConfigurationItemId(anyLong());

        verify(configurationItemMapper, never())
                .toResponse(any());
    }

    @Test
    void getServerDetail_서버상세정보가_없으면_예외가_발생한다() {
        // given
        when(configurationItemRepository.findById(100L))
                .thenReturn(Optional.of(configurationItem));

        when(serverDetailRepository.findByConfigurationItemId(100L))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                serverDetailQueryService.getServerDetail(100L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ServerErrorCode.SERVER_DETAIL_NOT_FOUND);

        verify(configurationItemMapper, never())
                .toResponse(any());
    }
}
