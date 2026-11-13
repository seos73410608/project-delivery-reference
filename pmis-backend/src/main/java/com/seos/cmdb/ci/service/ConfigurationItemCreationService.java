
package com.seos.cmdb.ci.service;

import com.seos.cmdb.ci.entity.ConfigurationItem;
import com.seos.cmdb.ci.entity.CiEnvironment;
import com.seos.cmdb.ci.entity.CiType;
import com.seos.cmdb.ci.mapper.ConfigurationItemMapper;
import com.seos.cmdb.ci.repository.ConfigurationItemRepository;
import com.seos.pmis.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ConfigurationItemCreationService {

    private final ConfigurationItemRepository configurationItemRepository;
    private final ConfigurationItemMapper configurationItemMapper;
    private final ConfigurationItemValidationService configurationItemValidationService;

    /**
     * 기존 공통 Configuration Item 생성
     */
    @Transactional
    public ConfigurationItem create(
            String ciName,
            Long ciTypeId,
            CiEnvironment environment,
            Long managerId,
            String location,
            String description
    ) {

        CiType ciType =
                configurationItemValidationService.resolveAssignableCiType(ciTypeId);

        User manager =
                configurationItemValidationService.resolveManager(managerId);

        return create(
                ciName,
                ciType,
                environment,
                manager,
                location,
                description
        );
    }

    /**
     * 검증이 완료된 CI Type과 담당자를 이용한 공통 CI 생성
     */
    @Transactional
    public ConfigurationItem create(
            String ciName,
            CiType ciType,
            CiEnvironment environment,
            User manager,
            String location,
            String description
    ) {

        String ciCode = generateCiCode();

        ConfigurationItem configurationItem =
                configurationItemMapper.toEntity(
                        ciName,
                        ciType,
                        environment,
                        manager,
                        location,
                        description,
                        ciCode
                );

        return configurationItemRepository.save(configurationItem);
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
