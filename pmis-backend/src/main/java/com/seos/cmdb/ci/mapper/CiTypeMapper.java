package com.seos.cmdb.ci.mapper;

import com.seos.cmdb.ci.dto.request.CreateCiTypeRequest;
import com.seos.cmdb.ci.dto.response.CiTypeResponse;
import com.seos.cmdb.ci.entity.CiType;
import org.springframework.stereotype.Component;

@Component
public class CiTypeMapper {

    /**
     * CI Type 생성
     */
    public CiType toEntity(
            CreateCiTypeRequest request,
            CiType parent
    ) {

        return CiType.builder()
                .code(request.getCode())
                .name(request.getName())
                .parent(parent)
                .description(request.getDescription())
                .assignable(request.getAssignable())
                .sortOrder(
                        request.getSortOrder() != null
                                ? request.getSortOrder()
                                : 0
                )
                .build();
    }

    /**
     * Entity → Response
     */
    public CiTypeResponse toResponse(CiType ciType) {

        return CiTypeResponse.builder()
                .id(ciType.getId())
                .code(ciType.getCode())
                .name(ciType.getName())
                .parentId(
                        ciType.getParent() != null
                                ? ciType.getParent().getId()
                                : null
                )
                .description(ciType.getDescription())
                .assignable(ciType.getAssignable())
                .enabled(ciType.getEnabled())
                .sortOrder(ciType.getSortOrder())
                .build();
    }
}