package com.seos.pmis.project.service;

import com.seos.pmis.common.exception.BusinessException;
import com.seos.pmis.common.exception.code.CommonErrorCode;
import com.seos.pmis.project.dto.request.CreateProjectRequest;
import com.seos.pmis.project.dto.request.ProjectSearchRequest;
import com.seos.pmis.project.dto.request.UpdateProjectRequest;
import com.seos.pmis.project.dto.response.ProjectResponse;
import com.seos.pmis.project.entity.Project;
import com.seos.pmis.project.entity.ProjectStatus;
import com.seos.pmis.project.mapper.ProjectMapper;
import com.seos.pmis.project.repository.ProjectRepository;
import com.seos.pmis.project.specification.ProjectSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    /**
     * 허용된 정렬 컬럼
     */
    private static final Set<String> ALLOWED_SORT_PROPERTIES = Set.of(
            "projectCode",
            "projectName",
            "customerName",
            "projectManager",
            "status",
            "priority",
            "startDate",
            "endDate",
            "createdAt",
            "updatedAt"
    );

    /**
     * 프로젝트 생성
     */
    @Transactional
    public ProjectResponse create(CreateProjectRequest request) {

        if (projectRepository.existsByProjectCode(request.getProjectCode())) {
            throw new BusinessException(CommonErrorCode.INVALID_REQUEST);
        }

        Project project = projectMapper.toEntity(request);

        // 생성 시 기본 상태
        project.changeStatus(ProjectStatus.PLANNING);

        Project savedProject = projectRepository.save(project);

        return projectMapper.toResponse(savedProject);
    }

    /**
     * 프로젝트 단건 조회
     */
    public ProjectResponse getProject(Long projectId) {

        Project project = findProject(projectId);

        return projectMapper.toResponse(project);
    }

    /**
     * 프로젝트 검색
     */
    public Page<ProjectResponse> searchProjects(
            ProjectSearchRequest request,
            Pageable pageable
    ) {

        validateSort(pageable);

        Specification<Project> specification = Specification
                .where(ProjectSpecification.projectCodeContains(request.getProjectCode()))
                .and(ProjectSpecification.projectNameContains(request.getProjectName()))
                .and(ProjectSpecification.customerNameContains(request.getCustomerName()))
                .and(ProjectSpecification.projectManagerContains(request.getProjectManager()))
                .and(ProjectSpecification.hasStatus(request.getStatus()))
                .and(ProjectSpecification.hasPriority(request.getPriority()))
                .and(ProjectSpecification.startDateFrom(request.getStartDateFrom()))
                .and(ProjectSpecification.startDateTo(request.getStartDateTo()))
                .and(ProjectSpecification.endDateFrom(request.getEndDateFrom()))
                .and(ProjectSpecification.endDateTo(request.getEndDateTo()));

        return projectRepository.findAll(specification, pageable)
                .map(projectMapper::toResponse);
    }

    /**
     * 프로젝트 수정
     */
    @Transactional
    public ProjectResponse update(
            Long projectId,
            UpdateProjectRequest request
    ) {

        Project project = findProject(projectId);

        project.update(
                request.getProjectName(),
                request.getCustomerName(),
                request.getProjectManager(),
                request.getDescription(),
                request.getStartDate(),
                request.getEndDate(),
                request.getStatus(),
                request.getPriority()
        );

        return projectMapper.toResponse(project);
    }

    /**
     * 프로젝트 삭제
     */
    @Transactional
    public void delete(Long projectId) {

        Project project = findProject(projectId);

        projectRepository.delete(project);
    }

    /**
     * 프로젝트 조회
     */
    private Project findProject(Long projectId) {

        return projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new BusinessException(CommonErrorCode.RESOURCE_NOT_FOUND)
                );
    }

    /**
     * 허용된 정렬 컬럼 검증
     */
    private void validateSort(Pageable pageable) {

        for (Sort.Order order : pageable.getSort()) {

            if (!ALLOWED_SORT_PROPERTIES.contains(order.getProperty())) {
                throw new BusinessException(CommonErrorCode.INVALID_REQUEST);
            }
        }
    }
}