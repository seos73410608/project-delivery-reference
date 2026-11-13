package com.seos.cmdb.ci.service;

import com.seos.cmdb.ci.dto.request.ChangeConfigurationItemStatusRequest;
import com.seos.cmdb.ci.dto.request.CreateConfigurationItemRequest;
import com.seos.cmdb.ci.dto.request.UpdateConfigurationItemRequest;
import com.seos.cmdb.ci.dto.response.ConfigurationItemResponse;
import com.seos.cmdb.ci.entity.ConfigurationItem;
import com.seos.cmdb.ci.mapper.ConfigurationItemMapper;
import com.seos.cmdb.ci.repository.ConfigurationItemRepository;
import com.seos.cmdb.ci.exception.ConfigurationItemErrorCode;
import com.seos.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConfigurationItemService {

    private final ConfigurationItemRepository configurationItemRepository;
    private final ConfigurationItemMapper configurationItemMapper;
    private final ConfigurationItemCreationService configurationItemCreationService;
    private final ConfigurationItemValidationService configurationItemValidationService;

    /**
     * Configuration Item 생성
     */
    @Transactional
    public ConfigurationItemResponse create(
            CreateConfigurationItemRequest request
    ) {

        ConfigurationItem configurationItem =
                configurationItemCreationService.create(
                        request.getCiName(),
                        request.getCiTypeId(),
                        request.getEnvironment(),
                        request.getManagerId(),
                        request.getLocation(),
                        request.getDescription()
                );

        return configurationItemMapper.toResponse(
                configurationItem
        );
    }

    /**
     * 전체 Configuration Item 조회
     */
    public List<ConfigurationItemResponse> getAll() {

        return configurationItemRepository
                .findAll(Sort.by(Sort.Direction.ASC, "ciCode"))
                .stream()
                .map(configurationItemMapper::toResponse)
                .toList();
    }

    /**
     * Configuration Item 단건 조회
     */
    public ConfigurationItemResponse getById(Long id) {

        return configurationItemMapper.toResponse(
                findConfigurationItem(id)
        );
    }

    /**
     * Configuration Item 기본 정보 수정
     */
    @Transactional
    public ConfigurationItemResponse update(
            Long id,
            UpdateConfigurationItemRequest request
    ) {

        ConfigurationItem configurationItem =
                findConfigurationItem(id);

        configurationItem.update(
                request.getCiName(),
                configurationItemValidationService.resolveAssignableCiType(
                        request.getCiTypeId()
                ),
                request.getEnvironment(),
                configurationItemValidationService.resolveManager(
                        request.getManagerId()
                ),
                request.getLocation(),
                request.getDescription()
        );

        return configurationItemMapper.toResponse(
                configurationItem
        );
    }

    /**
     * Configuration Item 상태 변경
     */
    @Transactional
    public ConfigurationItemResponse changeStatus(
            Long id,
            ChangeConfigurationItemStatusRequest request
    ) {

        ConfigurationItem configurationItem =
                findConfigurationItem(id);

        configurationItem.changeStatus(request.getStatus());

        return configurationItemMapper.toResponse(
                configurationItem
        );
    }

    /**
     * Configuration Item 조회 공통 처리
     */
    private ConfigurationItem findConfigurationItem(Long id) {

        return configurationItemRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        ConfigurationItemErrorCode.CI_NOT_FOUND
                ));
    }
}
