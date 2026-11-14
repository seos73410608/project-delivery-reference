
package com.seos.cmdb.server.service;

import com.seos.cmdb.ci.dto.response.ConfigurationItemResponse;
import com.seos.cmdb.ci.entity.ConfigurationItem;
import com.seos.cmdb.ci.mapper.ConfigurationItemMapper;
import com.seos.cmdb.ci.repository.ConfigurationItemRepository;
import com.seos.cmdb.server.dto.response.ServerRegistrationResponse;
import com.seos.cmdb.server.entity.ServerDetail;
import com.seos.cmdb.server.exception.ServerErrorCode;
import com.seos.cmdb.server.repository.ServerDetailRepository;
import com.seos.cmdb.ci.exception.ConfigurationItemErrorCode;
import com.seos.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ServerDetailQueryService {

    private static final String SERVER_CI_TYPE_CODE = "SERVER";

    private final ConfigurationItemRepository configurationItemRepository;
    private final ServerDetailRepository serverDetailRepository;
    private final ConfigurationItemMapper configurationItemMapper;

    /**
     * CI ID로 서버 상세정보 조회
     */
    public ServerRegistrationResponse getServerDetail(Long ciId) {

        // 1. CI 존재 여부 확인
        ConfigurationItem configurationItem = configurationItemRepository.findById(ciId)
                .orElseThrow(() -> new BusinessException(
                        ConfigurationItemErrorCode.CI_NOT_FOUND));

        // 2. SERVER 유형 확인
        if (!SERVER_CI_TYPE_CODE.equals(
                configurationItem.getCiType().getCode())) {
            throw new BusinessException(
                    ServerErrorCode.SERVER_CI_TYPE_REQUIRED);
        }

        // 3. 서버 상세정보 조회
        ServerDetail serverDetail = serverDetailRepository.findByConfigurationItemId(ciId)
                .orElseThrow(() -> new BusinessException(
                        ServerErrorCode.SERVER_DETAIL_NOT_FOUND));

        // 4. 공통 CI 응답 생성
        ConfigurationItemResponse ciResponse = configurationItemMapper.toResponse(configurationItem);

        // 5. 서버 상세정보 응답 생성
        ServerRegistrationResponse.ServerDetailResponse detailResponse = ServerRegistrationResponse.ServerDetailResponse
                .builder()
                .hostname(serverDetail.getHostname())
                .managementIp(serverDetail.getManagementIp())
                .serverCategory(serverDetail.getServerCategory())
                .osName(serverDetail.getOsName())
                .osVersion(serverDetail.getOsVersion())
                .cpuCores(serverDetail.getCpuCores())
                .memorySizeMb(serverDetail.getMemorySizeMb())
                .virtualizationPlatform(
                        serverDetail.getVirtualizationPlatform())
                .build();

        return ServerRegistrationResponse.from(
                ciResponse,
                detailResponse);
    }
}