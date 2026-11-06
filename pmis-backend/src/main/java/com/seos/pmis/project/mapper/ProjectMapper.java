
package com.seos.pmis.project.mapper;

import com.seos.pmis.project.dto.request.CreateProjectRequest;
import com.seos.pmis.project.dto.response.ProjectDetailResponse;
import com.seos.pmis.project.dto.response.ProjectResponse;
import com.seos.pmis.project.entity.Project;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectMapper {

    /**
     * Create DTO → Entity
     */
    public Project toEntity(CreateProjectRequest request) {

        return Project.builder()
                .projectCode(request.getProjectCode())
                .projectName(request.getProjectName())
                .customerName(request.getCustomerName())
                .projectManager(request.getProjectManager())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .priority(request.getPriority())
                .build();
    }

    /**
     * Entity → ProjectResponse
     *
     * 프로젝트 목록/일반 조회용
     */
    public ProjectResponse toResponse(Project project) {

        return ProjectResponse.builder()
                .id(project.getId())
                .projectCode(project.getProjectCode())
                .projectName(project.getProjectName())
                .customerName(project.getCustomerName())
                .projectManager(project.getProjectManager())
                .description(project.getDescription())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .status(project.getStatus())
                .priority(project.getPriority())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }

    /**
     * Entity → ProjectDetailResponse
     *
     * 프로젝트 상세 조회용
     *
     * 프로젝트 기본 정보만 매핑하며,
     * 진척률 집계 정보는 ProjectDetailService에서 추가합니다.
     */
    public ProjectDetailResponse toDetailResponse(Project project) {

        return ProjectDetailResponse.builder()
                .id(project.getId())
                .projectCode(project.getProjectCode())
                .projectName(project.getProjectName())
                .customerName(project.getCustomerName())
                .projectManager(project.getProjectManager())
                .description(project.getDescription())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .status(project.getStatus())
                .priority(project.getPriority())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }
}
