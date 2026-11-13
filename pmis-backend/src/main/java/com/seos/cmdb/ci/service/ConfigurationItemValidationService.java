
package com.seos.cmdb.ci.service;

import com.seos.cmdb.ci.entity.CiType;
import com.seos.cmdb.ci.exception.ConfigurationItemErrorCode;
import com.seos.cmdb.ci.exception.CiTypeErrorCode;
import com.seos.cmdb.ci.repository.CiTypeRepository;
import com.seos.common.exception.BusinessException;
import com.seos.pmis.user.entity.User;
import com.seos.pmis.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConfigurationItemValidationService {

    private final CiTypeRepository ciTypeRepository;
    private final UserRepository userRepository;

    /**
     * 사용 가능한 CI Type 조회
     */
    public CiType resolveAssignableCiType(Long ciTypeId) {

        CiType ciType = ciTypeRepository.findById(ciTypeId)
                .orElseThrow(() -> new BusinessException(
                        CiTypeErrorCode.CI_TYPE_NOT_FOUND
                ));

        if (!Boolean.TRUE.equals(ciType.getEnabled())) {
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
    public User resolveManager(Long managerId) {

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
}
