package com.seos.cmdb.ci.service;

import com.seos.cmdb.ci.dto.request.ChangeConfigurationItemStatusRequest;
import com.seos.cmdb.ci.dto.request.CreateConfigurationItemRequest;
import com.seos.cmdb.ci.dto.request.UpdateConfigurationItemRequest;
import com.seos.cmdb.ci.dto.response.ConfigurationItemResponse;
import com.seos.cmdb.ci.entity.ConfigurationItem;
import com.seos.cmdb.ci.entity.CiType;
import com.seos.cmdb.ci.exception.ConfigurationItemErrorCode;
import com.seos.cmdb.ci.exception.CiTypeErrorCode;
import com.seos.cmdb.ci.mapper.ConfigurationItemMapper;
import com.seos.cmdb.ci.repository.CiTypeRepository;
import com.seos.cmdb.ci.repository.ConfigurationItemRepository;
import com.seos.common.exception.BusinessException;
import com.seos.pmis.user.entity.User;
import com.seos.pmis.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConfigurationItemService {

    private final ConfigurationItemRepository configurationItemRepository;
    private final CiTypeRepository ciTypeRepository;
    private final UserRepository userRepository;
    private final ConfigurationItemMapper configurationItemMapper;

    /**
     * Configuration Item 생성
     */
    @Transactional
    public ConfigurationItemResponse create(
            CreateConfigurationItemRequest request
    ) {

        CiType ciType = resolveAssignableCiType(request.getCiTypeId());

        User manager = resolveManager(request.getManagerId());

        String ciCode = generateCiCode();

        ConfigurationItem configurationItem =
                configurationItemMapper.toEntity(
                        request,
                        ciType,
                        manager,
                        ciCode
                );

        ConfigurationItem savedConfigurationItem =
                configurationItemRepository.save(configurationItem);

        return configurationItemMapper.toResponse(
                savedConfigurationItem
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

        CiType ciType = resolveAssignableCiType(request.getCiTypeId());

        User manager = resolveManager(request.getManagerId());

        configurationItem.update(
                request.getCiName(),
                ciType,
                request.getEnvironment(),
                manager,
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

    /**
     * 사용 가능한 CI Type 조회
     */
    private CiType resolveAssignableCiType(Long ciTypeId) {

        CiType ciType = ciTypeRepository.findById(ciTypeId)
                .orElseThrow(() -> new BusinessException(
                        CiTypeErrorCode.CI_TYPE_NOT_FOUND
                ));

        if (!ciType.getEnabled()) {
            throw new BusinessException(
                    CiTypeErrorCode.CI_TYPE_DISABLED
            );
        }

        if (!Boolean.TRUE.equals(ciType.getAssignable())) {
            throw new BusinessException(
                    ConfigurationItemErrorCode.CI_TYPE_NOT_ASSIGNABLE
            );
        }

        return ciType;
    }

    /**
     * 담당자 조회
     */
    private User resolveManager(Long managerId) {

        if (managerId == null) {
            return null;
        }

        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new BusinessException(
                        ConfigurationItemErrorCode.CI_MANAGER_NOT_FOUND
                ));

        if (!Boolean.TRUE.equals(manager.getEnabled())) {
            throw new BusinessException(
                    ConfigurationItemErrorCode.CI_MANAGER_DISABLED
            );
        }

        return manager;
    }

    /**
     * CI 코드 발급
     */
    private String generateCiCode() {

        long sequenceValue =
                configurationItemRepository.nextCiCodeSequenceValue();

        return String.format(
                Locale.ROOT,
                "CI-%06d",
                sequenceValue
        );
    }
}