package com.seos.pmis.evidence.mapper;

import com.seos.pmis.evidence.dto.request.InspectionCreateRequest;
import com.seos.pmis.evidence.dto.request.InspectionUpdateRequest;
import com.seos.pmis.evidence.dto.response.InspectionResponse;
import com.seos.pmis.evidence.entity.Inspection;
import com.seos.pmis.project.entity.Project;
import org.springframework.stereotype.Component;

/**
 * Inspection Mapper
 *
 * Inspection의
 * Entity ↔ DTO 변환을 담당한다.
 *
 * Mapper의 책임:
 * - CreateRequest → Entity
 * - Entity → Response
 * - UpdateRequest → Entity update
 *
 * Mapper에서는 비즈니스 로직을 처리하지 않는다.
 *
 * 예:
 * - Project 존재 여부 검증
 * - Project와 Inspection 관계 검증
 * - Inspection 상태 전이 검증
 *
 * 위 로직은 Service에서 처리한다.
 */
@Component
public class InspectionMapper {

    /**
     * Inspection 생성 Request를 Entity로 변환한다.
     *
     * project는 Service에서 조회하여 전달받는다.
     *
     * Inspection 상태는 Entity의 기본값인
     * PLANNED를 사용한다.
     */
    public Inspection toEntity(
            InspectionCreateRequest request,
            Project project
    ) {
        return Inspection.builder()
                .project(project)
                .title(request.getTitle())
                .description(request.getDescription())
                .inspectionDate(request.getInspectionDate())
                .inspectorName(request.getInspectorName())
                .resultRemark(request.getResultRemark())
                .build();
    }

    /**
     * Inspection Entity를 Response DTO로 변환한다.
     */
    public InspectionResponse toResponse(
            Inspection entity
    ) {
        return InspectionResponse.builder()
                .id(entity.getId())
                .projectId(entity.getProject().getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .inspectionDate(entity.getInspectionDate())
                .startedAt(entity.getStartedAt())
                .completedAt(entity.getCompletedAt())
                .status(entity.getStatus())
                .inspectorName(entity.getInspectorName())
                .resultRemark(entity.getResultRemark())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * Inspection 수정 Request를 Entity에 반영한다.
     *
     * 상태(status),
     * 시작 시각(startedAt),
     * 완료 시각(completedAt)은 변경하지 않는다.
     *
     * Workflow 상태 변경은 별도의 Status API를 통해 처리한다.
     */
    public void updateEntity(
            Inspection entity,
            InspectionUpdateRequest request
    ) {
        entity.update(
                request.getTitle(),
                request.getDescription(),
                request.getInspectionDate(),
                request.getInspectorName(),
                request.getResultRemark()
        );
    }
}