
package com.seos.pmis.project.service;

import com.seos.common.exception.BusinessException;
import com.seos.common.exception.code.CommonErrorCode;
import com.seos.pmis.project.dto.response.ProjectDetailResponse;
import com.seos.pmis.project.entity.Project;
import com.seos.pmis.project.mapper.ProjectMapper;
import com.seos.pmis.project.repository.ProjectRepository;
import com.seos.pmis.task.dto.response.TaskProgressAggregation;
import com.seos.pmis.task.entity.Task;
import com.seos.pmis.task.repository.TaskRepository;
import com.seos.pmis.task.service.TaskProgressAggregationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectDetailService {

        private final ProjectRepository projectRepository;
        private final ProjectMapper projectMapper;
        private final TaskRepository taskRepository;
        private final TaskProgressAggregationService taskProgressAggregationService;

        /**
         * 프로젝트 상세 조회
         *
         * 프로젝트 기본 정보와 전체 Task 기반 진척률 집계 정보를 조회합니다.
         */
        public ProjectDetailResponse getProjectDetail(Long projectId) {

                Project project = projectRepository.findById(projectId)
                                .orElseThrow(() -> new BusinessException(
                                                CommonErrorCode.RESOURCE_NOT_FOUND));

                // 프로젝트 전체 Task 조회 (WBS 미할당 Task 포함)
                List<Task> tasks = taskRepository.findByProject_IdOrderBySortOrderAsc(projectId);

                // 프로젝트 전체 Task 기반 진척률 집계
                TaskProgressAggregation aggregation = taskProgressAggregationService.aggregateProject(tasks);

                // 프로젝트 기본 정보 매핑 후 집계 정보 추가
                return projectMapper.toDetailResponse(project)
                                .toBuilder()
                                .progressRate(aggregation.getProgressRate())
                                .totalPlannedManDays(aggregation.getTotalPlannedManDays())
                                .weightedCompletedManDays(aggregation.getWeightedCompletedManDays())
                                .missingEffortTaskCount(aggregation.getMissingEffortTaskCount())
                                .build();
        }
}
