package com.seos.pmis.evidence.mapper;

import com.seos.pmis.evidence.dto.request.EvidenceRequirementCreateRequest;
import com.seos.pmis.evidence.dto.request.EvidenceRequirementUpdateRequest;
import com.seos.pmis.evidence.dto.response.EvidenceRequirementResponse;
import com.seos.pmis.evidence.entity.EvidenceRequirement;
import com.seos.pmis.project.entity.Project;
import com.seos.pmis.wbs.entity.Wbs;
import org.springframework.stereotype.Component;
import com.seos.pmis.evidence.enums.RequirementStatus;

/**
 * Evidence Requirement Mapper
 *
 * Evidence Requirement의
 * Entity ↔ DTO 변환을 담당한다.
 *
 * Mapper의 책임:
 * - CreateRequest → Entity
 * - Entity → Response
 * - UpdateRequest → Entity update
 *
 * Service의 비즈니스 로직은 Mapper에서 처리하지 않는다.
 */
@Component
public class EvidenceRequirementMapper {

    /**
     * CreateRequest → EvidenceRequirement Entity
     *
     * 프로젝트와 WBS는 Service에서 조회한 Entity를 전달받는다.
     *
     * requirementKey와 status는 Backend에서 관리하므로
     * Request에서 직접 전달받지 않는다.
     *
     * @param request 생성 요청
     * @param project 프로젝트 Entity
     * @param wbs WBS Entity
     * @param requirementKey Backend에서 생성한 Requirement Key
     * @return EvidenceRequirement Entity
     */
    public EvidenceRequirement toEntity(
            EvidenceRequirementCreateRequest request,
            Project project,
            Wbs wbs,
            String requirementKey
    ) {
        return EvidenceRequirement.builder()
                .project(project)
                .wbs(wbs)
                .requirementKey(requirementKey)
                .title(request.getTitle())
                .description(request.getDescription())
                .requirementType(request.getRequirementType())
                .required(request.getRequired())
                .dueDate(request.getDueDate())
                .status(RequirementStatus.PENDING)
                .sortOrder(request.getSortOrder())
                .build();
    }

    /**
     * Entity → Response DTO
     *
     * Entity의 연관관계에서 필요한 ID를 추출하여
     * Response DTO로 변환한다.
     *
     * @param entity Evidence Requirement Entity
     * @return Response DTO
     */
    public EvidenceRequirementResponse toResponse(
            EvidenceRequirement entity
    ) {
        return EvidenceRequirementResponse.builder()
                .id(entity.getId())
                .projectId(entity.getProject().getId())
                .wbsId(
                        entity.getWbs() != null
                                ? entity.getWbs().getId()
                                : null
                )
                .requirementKey(entity.getRequirementKey())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .requirementType(entity.getRequirementType())
                .required(entity.getRequired())
                .dueDate(entity.getDueDate())
                .status(entity.getStatus())
                .sortOrder(entity.getSortOrder())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * UpdateRequest → Entity
     *
     * 프로젝트, Requirement Key, Status는 변경하지 않는다.
     *
     * WBS는 Service에서 조회한 Entity를 전달받는다.
     *
     * @param entity 수정 대상 Entity
     * @param request 수정 요청
     * @param wbs WBS Entity
     */
    public void updateEntity(
            EvidenceRequirement entity,
            EvidenceRequirementUpdateRequest request,
            Wbs wbs
    ) {
        entity.update(
                wbs,
                request.getTitle(),
                request.getDescription(),
                request.getRequirementType(),
                request.getRequired(),
                request.getDueDate(),
                request.getSortOrder()
        );
    }
}