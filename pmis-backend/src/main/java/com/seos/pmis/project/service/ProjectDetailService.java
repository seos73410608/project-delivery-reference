package com.seos.pmis.project.service;

import com.seos.pmis.common.exception.BusinessException;
import com.seos.pmis.common.exception.code.CommonErrorCode;
import com.seos.pmis.project.dto.response.ProjectDetailResponse;
import com.seos.pmis.project.entity.Project;
import com.seos.pmis.project.mapper.ProjectMapper;
import com.seos.pmis.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectDetailService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    /**
     * 프로젝트 상세 조회
     *
     * 프로젝트 기본 정보와 상세 화면에 필요한
     * 프로젝트 관리 정보를 조회합니다.
     */
    public ProjectDetailResponse getProjectDetail(Long projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new BusinessException(
                                CommonErrorCode.RESOURCE_NOT_FOUND
                        )
                );

        return projectMapper.toDetailResponse(project);
    }
}