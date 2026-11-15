
package com.seos.cmdb.server.service;

import com.seos.cmdb.ci.dto.response.ConfigurationItemResponse;
import com.seos.cmdb.ci.entity.ConfigurationItem;
import com.seos.cmdb.ci.exception.ConfigurationItemErrorCode;
import com.seos.cmdb.ci.mapper.ConfigurationItemMapper;
import com.seos.cmdb.ci.repository.ConfigurationItemRepository;
import com.seos.cmdb.server.dto.request.UpdateServerDetailRequest;
import com.seos.cmdb.server.dto.response.ServerRegistrationResponse;
import com.seos.cmdb.server.entity.ServerDetail;
import com.seos.cmdb.server.exception.ServerErrorCode;
import com.seos.cmdb.server.repository.ServerDetailRepository;
import com.seos.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ServerDetailUpdateService {

    private static final String SERVER_CI_TYPE_CODE = "SERVER";

    private final ConfigurationItemRepository configurationItemRepository;
    private final ServerDetailRepository serverDetailRepository;
    private final ConfigurationItemMapper configurationItemMapper;

    /**
     * 서버 상세정보 전체 수정
     */
    public ServerRegistrationResponse updateServerDetail(
            Long ciId,
            UpdateServerDetailRequest request
    ) {

        // 1. CI 존재 여부 확인
        ConfigurationItem configurationItem =
                configurationItemRepository.findById(ciId)
                        .orElseThrow(() -> new BusinessException(
                                ConfigurationItemErrorCode.CI_NOT_FOUND
                        ));

        // 2. SERVER 유형 확인
        if (!SERVER_CI_TYPE_CODE.equals(
                configurationItem.getCiType().getCode())) {
            throw new BusinessException(
                    ServerErrorCode.SERVER_CI_TYPE_REQUIRED
            );
        }

        // 3. 서버 상세정보 조회
        ServerDetail serverDetail =
                serverDetailRepository.findByConfigurationItemId(ciId)
                        .orElseThrow(() -> new BusinessException(
                                ServerErrorCode.SERVER_DETAIL_NOT_FOUND
                        ));

        // 4. 서버 상세정보 전체 수정
        serverDetail.updateDetails(
                request.getHostname(),
                request.getServerCategory(),
                request.getManagementIp(),
                request.getOsName(),
                request.getOsVersion(),
                request.getCpuCores(),
                request.getMemorySizeMb(),
                request.getVirtualizationPlatform()
        );

        // 5. 공통 CI 응답 생성
        ConfigurationItemResponse ciResponse =
                configurationItemMapper.toResponse(configurationItem);

        // 6. 서버 상세정보 응답 생성
        ServerRegistrationResponse.ServerDetailResponse detailResponse =
                ServerRegistrationResponse.ServerDetailResponse.builder()
                        .hostname(serverDetail.getHostname())
                        .managementIp(serverDetail.getManagementIp())
                        .serverCategory(serverDetail.getServerCategory())
                        .osName(serverDetail.getOsName())
                        .osVersion(serverDetail.getOsVersion())
                        .cpuCores(serverDetail.getCpuCores())
                        .memorySizeMb(serverDetail.getMemorySizeMb())
                        .virtualizationPlatform(
                                serverDetail.getVirtualizationPlatform()
                        )
                        .build();

        return ServerRegistrationResponse.from(
                ciResponse,
                detailResponse
        );
    }
}
