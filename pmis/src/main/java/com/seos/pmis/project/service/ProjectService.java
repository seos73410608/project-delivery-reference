package com.seos.pmis.project.service;

import com.seos.pmis.common.exception.BusinessException;
import com.seos.pmis.common.exception.code.CommonErrorCode;
import com.seos.pmis.project.dto.request.CreateProjectRequest;
import com.seos.pmis.project.dto.request.UpdateProjectRequest;
import com.seos.pmis.project.dto.response.ProjectResponse;
import com.seos.pmis.project.entity.Project;
import com.seos.pmis.project.entity.ProjectStatus;
import com.seos.pmis.project.mapper.ProjectMapper;
import com.seos.pmis.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

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
     * 프로젝트 목록 조회
     */
    public Page<ProjectResponse> getProjects(Pageable pageable) {

        return projectRepository.findAll(pageable)
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
}