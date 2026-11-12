
package com.seos.cmdb.ci.service;

import com.seos.cmdb.ci.dto.request.CreateCiTypeRequest;
import com.seos.cmdb.ci.dto.request.UpdateCiTypeRequest;
import com.seos.cmdb.ci.dto.response.CiTypeResponse;
import com.seos.cmdb.ci.entity.CiType;
import com.seos.cmdb.ci.exception.CiTypeErrorCode;
import com.seos.cmdb.ci.mapper.CiTypeMapper;
import com.seos.cmdb.ci.repository.CiTypeRepository;
import com.seos.cmdb.ci.repository.ConfigurationItemRepository;
import com.seos.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CiTypeService {

    private final CiTypeRepository ciTypeRepository;
    private final ConfigurationItemRepository configurationItemRepository;
    private final CiTypeMapper ciTypeMapper;

    /**
     * CI Type 생성
     */
    @Transactional
    public CiTypeResponse create(CreateCiTypeRequest request) {

        if (ciTypeRepository.existsByCode(request.getCode())) {
            throw new BusinessException(
                    CiTypeErrorCode.CI_TYPE_CODE_DUPLICATED
            );
        }

        CiType parent = resolveParent(request.getParentId());

        CiType ciType = ciTypeMapper.toEntity(request, parent);

        return ciTypeMapper.toResponse(
                ciTypeRepository.save(ciType)
        );
    }

    /**
     * 전체 CI Type 조회
     */
    public List<CiTypeResponse> getAll() {

        return ciTypeRepository
                .findAllByOrderBySortOrderAscNameAsc()
                .stream()
                .map(ciTypeMapper::toResponse)
                .toList();
    }

    /**
     * CI Type 단건 조회
     */
    public CiTypeResponse getById(Long id) {

        return ciTypeMapper.toResponse(findCiType(id));
    }

    /**
     * CI Type 수정
     */
    @Transactional
    public CiTypeResponse update(
            Long id,
            UpdateCiTypeRequest request
    ) {

        CiType ciType = findCiType(id);

        CiType parent = resolveParent(request.getParentId());

        validateParentChange(ciType, parent);

        // CI가 사용 중인 Type은 일반 분류 Type으로 변경할 수 없다.
        if (Boolean.TRUE.equals(ciType.getAssignable())
                && Boolean.FALSE.equals(request.getAssignable())
                && configurationItemRepository.existsByCiType_Id(id)) {

            throw new BusinessException(
                    CiTypeErrorCode.CI_TYPE_HAS_CONFIGURATION_ITEMS
            );
        }

        ciType.update(
                request.getName(),
                parent,
                request.getDescription(),
                request.getAssignable(),
                request.getSortOrder()
        );

        return ciTypeMapper.toResponse(ciType);
    }

    /**
     * CI Type 활성화
     */
    @Transactional
    public CiTypeResponse enable(Long id) {

        CiType ciType = findCiType(id);

        if (ciType.getParent() != null
                && !ciType.getParent().getEnabled()) {

            throw new BusinessException(
                    CiTypeErrorCode.CI_TYPE_PARENT_DISABLED
            );
        }

        ciType.enable();

        return ciTypeMapper.toResponse(ciType);
    }

    /**
     * CI Type 비활성화
     */
    @Transactional
    public void disable(Long id) {

        CiType ciType = findCiType(id);

        if (ciTypeRepository.existsByParent_IdAndEnabledTrue(id)) {
            throw new BusinessException(
                    CiTypeErrorCode.CI_TYPE_HAS_ACTIVE_CHILDREN
            );
        }

        // CI가 사용 중인 Type은 비활성화할 수 없다.
        if (configurationItemRepository.existsByCiType_Id(id)) {
            throw new BusinessException(
                    CiTypeErrorCode.CI_TYPE_HAS_CONFIGURATION_ITEMS
            );
        }

        ciType.disable();
    }

    /**
     * CI Type 삭제
     */
    @Transactional
    public void delete(Long id) {

        CiType ciType = findCiType(id);

        if (ciTypeRepository.countByParent_Id(id) > 0) {
            throw new BusinessException(
                    CiTypeErrorCode.CI_TYPE_HAS_CHILDREN
            );
        }

        if (configurationItemRepository.existsByCiType_Id(id)) {
            throw new BusinessException(
                    CiTypeErrorCode.CI_TYPE_IN_USE
            );
        }

        ciTypeRepository.delete(ciType);
    }

    /**
     * CI Type 조회 공통 처리
     */
    private CiType findCiType(Long id) {

        return ciTypeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        CiTypeErrorCode.CI_TYPE_NOT_FOUND
                ));
    }

    /**
     * 상위 CI Type 조회
     */
    private CiType resolveParent(Long parentId) {

        if (parentId == null) {
            return null;
        }

        CiType parent = findCiType(parentId);

        if (!parent.getEnabled()) {
            throw new BusinessException(
                    CiTypeErrorCode.CI_TYPE_PARENT_DISABLED
            );
        }

        return parent;
    }

    /**
     * 부모 변경 시 자기 참조 및 순환 참조 검증
     */
    private void validateParentChange(
            CiType target,
            CiType newParent
    ) {

        if (newParent == null) {
            return;
        }

        if (target.getId().equals(newParent.getId())) {
            throw new BusinessException(
                    CiTypeErrorCode.CI_TYPE_SELF_PARENT
            );
        }

        CiType current = newParent;

        while (current != null) {

            if (target.getId().equals(current.getId())) {
                throw new BusinessException(
                        CiTypeErrorCode.CI_TYPE_CIRCULAR_REFERENCE
                );
            }

            current = current.getParent();
        }
    }
}
