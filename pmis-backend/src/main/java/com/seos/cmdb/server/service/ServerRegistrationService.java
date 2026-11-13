package com.seos.cmdb.server.service;

import com.seos.cmdb.ci.dto.response.ConfigurationItemResponse;
import com.seos.cmdb.ci.entity.CiType;
import com.seos.cmdb.ci.entity.ConfigurationItem;
import com.seos.cmdb.ci.mapper.ConfigurationItemMapper;
import com.seos.cmdb.ci.service.ConfigurationItemCreationService;
import com.seos.cmdb.ci.service.ConfigurationItemValidationService;
import com.seos.common.exception.BusinessException;
import com.seos.cmdb.server.dto.request.CreateServerRequest;
import com.seos.cmdb.server.dto.response.ServerRegistrationResponse;
import com.seos.cmdb.server.entity.ServerDetail;
import com.seos.cmdb.server.exception.ServerErrorCode;
import com.seos.cmdb.server.repository.ServerDetailRepository;
import com.seos.pmis.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ServerRegistrationService {

    private static final String SERVER_CI_TYPE_CODE = "SERVER";

    private final ConfigurationItemCreationService configurationItemCreationService;
    private final ConfigurationItemValidationService configurationItemValidationService;
    private final ConfigurationItemMapper configurationItemMapper;
    private final ServerDetailRepository serverDetailRepository;

    /**
     * 서버 등록
     */
    @Transactional
    public ServerRegistrationResponse register(CreateServerRequest request) {

        // 1. CI Type 검증
        CiType ciType =
                configurationItemValidationService.resolveAssignableCiType(
                        request.getCiTypeId()
                );

        if (!SERVER_CI_TYPE_CODE.equals(ciType.getCode())) {
            throw new BusinessException(
                    ServerErrorCode.SERVER_CI_TYPE_REQUIRED
            );
        }

        // 2. 담당자 검증
        User manager =
                configurationItemValidationService.resolveManager(
                        request.getManagerId()
                );

        // 3. 공통 CI 생성
        ConfigurationItem configurationItem =
                configurationItemCreationService.create(
                        request.getCiName(),
                        ciType,
                        request.getEnvironment(),
                        manager,
                        request.getLocation(),
                        request.getDescription()
                );

        // 4. 서버 상세정보 생성
        ServerDetail serverDetail =
                ServerDetail.builder()
                        .configurationItem(configurationItem)
                        .hostname(request.getHostname())
                        .serverCategory(request.getServerCategory())
                        .managementIp(request.getManagementIp())
                        .osName(request.getOsName())
                        .osVersion(request.getOsVersion())
                        .cpuCores(request.getCpuCores())
                        .memorySizeMb(request.getMemorySizeMb())
                        .virtualizationPlatform(
                                request.getVirtualizationPlatform()
                        )
                        .build();

        serverDetailRepository.save(serverDetail);

        // 5. 통합 응답 반환
        ConfigurationItemResponse ciResponse =
                configurationItemMapper.toResponse(configurationItem);

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
