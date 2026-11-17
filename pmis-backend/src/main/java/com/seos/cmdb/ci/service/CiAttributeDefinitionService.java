
package com.seos.cmdb.ci.service;

import com.seos.cmdb.ci.dto.response.CiAttributeDefinitionResponse;
import com.seos.cmdb.ci.entity.AttributeNodeType;
import com.seos.cmdb.ci.entity.CiAttributeDefinition;
import com.seos.cmdb.ci.entity.CiType;
import com.seos.cmdb.ci.exception.CiTypeErrorCode;
import com.seos.cmdb.ci.mapper.CiAttributeDefinitionMapper;
import com.seos.cmdb.ci.repository.CiAttributeDefinitionRepository;
import com.seos.cmdb.ci.repository.CiTypeRepository;
import com.seos.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CiAttributeDefinitionService {

    private final CiAttributeDefinitionRepository ciAttributeDefinitionRepository;
    private final CiTypeRepository ciTypeRepository;
    private final CiAttributeDefinitionMapper ciAttributeDefinitionMapper;

    /**
     * CI Type에 적용되는 속성 정의 조회
     */
    public List<CiAttributeDefinitionResponse> getEffectiveDefinitions(
            Long ciTypeId
    ) {

        List<CiType> allCiTypes =
                ciTypeRepository.findAllByOrderBySortOrderAscNameAsc();

        Map<Long, CiType> ciTypeMap = allCiTypes.stream()
                .collect(Collectors.toMap(
                        CiType::getId,
                        ciType -> ciType
                ));

        CiType targetCiType = ciTypeMap.get(ciTypeId);

        if (targetCiType == null) {
            throw new BusinessException(
                    CiTypeErrorCode.CI_TYPE_NOT_FOUND
            );
        }

        if (!Boolean.TRUE.equals(targetCiType.getEnabled())) {
            throw new BusinessException(
                    CiTypeErrorCode.CI_TYPE_DISABLED
            );
        }

        Set<Long> inheritedCiTypeIds =
                collectInheritedCiTypeIds(targetCiType, ciTypeMap);

        List<CiAttributeDefinition> definitions =
                ciAttributeDefinitionRepository.findEffectiveDefinitions(
                        inheritedCiTypeIds
                );

        return buildTree(definitions);
    }

    /**
     * 현재 CI Type과 모든 상위 CI Type ID 수집
     */
    private Set<Long> collectInheritedCiTypeIds(
            CiType targetCiType,
            Map<Long, CiType> ciTypeMap
    ) {

        Set<Long> inheritedIds = new HashSet<>();

        CiType current = targetCiType;

        while (current != null) {

            if (!inheritedIds.add(current.getId())) {
                throw new IllegalStateException(
                        "CI Type 계층에 순환 참조가 존재합니다."
                );
            }

            Long parentId = current.getParent() != null
                    ? current.getParent().getId()
                    : null;

            if (parentId == null) {
                current = null;
            } else {
                current = ciTypeMap.get(parentId);

                if (current == null) {
                    throw new IllegalStateException(
                            "CI Type 상위 계층을 찾을 수 없습니다. parentId=" + parentId
                    );
                }
            }
        }

        return inheritedIds;
    }

    /**
     * GROUP / FIELD 트리 구성
     */
    private List<CiAttributeDefinitionResponse> buildTree(
            List<CiAttributeDefinition> definitions
    ) {

        Map<Long, List<CiAttributeDefinition>> childrenMap =
                new HashMap<>();

        for (CiAttributeDefinition definition : definitions) {

            Long parentId = definition.getParent() != null
                    ? definition.getParent().getId()
                    : null;

            childrenMap.computeIfAbsent(
                    parentId,
                    key -> new ArrayList<>()
            ).add(definition);
        }

        Comparator<CiAttributeDefinition> comparator =
                Comparator.comparing(
                                CiAttributeDefinition::getDisplayOrder,
                                Comparator.nullsLast(Integer::compareTo)
                        )
                        .thenComparing(CiAttributeDefinition::getId);

        childrenMap.values().forEach(
                children -> children.sort(comparator)
        );

        List<CiAttributeDefinitionResponse> result =
                new ArrayList<>();

        for (CiAttributeDefinition definition :
                childrenMap.getOrDefault(null, List.of())) {

            if (definition.getNodeType() == AttributeNodeType.GROUP) {

                result.add(buildGroupTree(
                        definition,
                        childrenMap
                ));
            }
        }

        return result;
    }

    /**
     * GROUP 하위 트리 재귀 구성
     */
    private CiAttributeDefinitionResponse buildGroupTree(
            CiAttributeDefinition group,
            Map<Long, List<CiAttributeDefinition>> childrenMap
    ) {

        List<CiAttributeDefinitionResponse> children =
                new ArrayList<>();

        for (CiAttributeDefinition child :
                childrenMap.getOrDefault(group.getId(), List.of())) {

            if (child.getNodeType() == AttributeNodeType.GROUP) {

                children.add(buildGroupTree(
                        child,
                        childrenMap
                ));

            } else if (child.getNodeType() == AttributeNodeType.FIELD) {

                children.add(
                        ciAttributeDefinitionMapper.toResponse(child)
                );
            }
        }

        return ciAttributeDefinitionMapper.toResponse(
                group,
                children
        );
    }
}
