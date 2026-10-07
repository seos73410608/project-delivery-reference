package com.seos.pmis.evidence.mapper;

import com.seos.pmis.evidence.dto.request.InspectionItemCreateRequest;
import com.seos.pmis.evidence.dto.request.InspectionItemUpdateRequest;
import com.seos.pmis.evidence.dto.response.InspectionItemResponse;
import com.seos.pmis.evidence.entity.Evidence;
import com.seos.pmis.evidence.entity.EvidenceRequirement;
import com.seos.pmis.evidence.entity.Inspection;
import com.seos.pmis.evidence.entity.InspectionItem;
import org.springframework.stereotype.Component;

/**
 * Inspection Item Mapper
 *
 * Inspection Item Entity와
 * Request / Response DTO 간의 변환을 담당한다.
 *
 * Mapper는 Entity 조회나 비즈니스 로직을 수행하지 않는다.
 *
 * Entity 조회 및 관계 검증은 Service에서 수행하고,
 * Mapper에는 이미 조회 및 검증된 Entity를 전달한다.
 */
@Component
public class InspectionItemMapper {

    /**
     * Inspection Item 생성
     *
     * Client Request와
     * Service에서 조회한 연관 Entity를 기반으로
     * InspectionItem Entity를 생성한다.
     *
     * result는 Entity의 기본값인 PENDING을 사용한다.
     */
    public InspectionItem toEntity(
            InspectionItemCreateRequest request,
            Inspection inspection,
            EvidenceRequirement requirement,
            Evidence evidence
    ) {
        return InspectionItem.builder()
                .inspection(inspection)
                .requirement(requirement)
                .evidence(evidence)
                .remark(request.getRemark())
                .sortOrder(request.getSortOrder())
                .build();
    }

    /**
     * Inspection Item Entity → Response DTO
     *
     * Inspection Detail 화면에서
     * Requirement와 Evidence 정보를 함께 표시할 수 있도록
     * 관련 Entity의 주요 표시 정보를 포함한다.
     */
    public InspectionItemResponse toResponse(
            InspectionItem entity
    ) {
        EvidenceRequirement requirement = entity.getRequirement();
        Evidence evidence = entity.getEvidence();

        return InspectionItemResponse.builder()
                .id(entity.getId())
                .inspectionId(entity.getInspection().getId())
                .requirementId(requirement.getId())
                .requirementKey(requirement.getRequirementKey())
                .requirementTitle(requirement.getTitle())
                .evidenceId(
                        evidence != null
                                ? evidence.getId()
                                : null
                )
                .evidenceKey(
                        evidence != null
                                ? evidence.getEvidenceKey()
                                : null
                )
                .evidenceTitle(
                        evidence != null
                                ? evidence.getTitle()
                                : null
                )
                .result(entity.getResult())
                .remark(entity.getRemark())
                .sortOrder(entity.getSortOrder())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * Inspection Item 수정
     *
     * 기본 정보와
     * 검사 대상 Requirement / Evidence를 반영한다.
     *
     * 검사 결과(result)는 수정하지 않는다.
     * 결과 변경은 별도의 결과 변경 API를 통해 처리한다.
     */
    public void updateEntity(
            InspectionItem entity,
            InspectionItemUpdateRequest request,
            EvidenceRequirement requirement,
            Evidence evidence
    ) {
        entity.changeRequirement(requirement);
        entity.changeEvidence(evidence);

        entity.update(
                request.getRemark(),
                request.getSortOrder()
        );
    }
}