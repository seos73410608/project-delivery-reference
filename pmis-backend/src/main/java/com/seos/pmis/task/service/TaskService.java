
package com.seos.pmis.task.service;

import com.seos.common.exception.BusinessException;
import com.seos.common.exception.code.CommonErrorCode;
import com.seos.common.search.SearchPageableFactory;
import com.seos.pmis.auth.security.principal.UserPrincipal;
import com.seos.pmis.project.entity.Project;
import com.seos.pmis.project.repository.ProjectRepository;
import com.seos.pmis.schedule.entity.Schedule;
import com.seos.pmis.schedule.repository.ScheduleRepository;
import com.seos.pmis.task.dto.request.TaskCreateRequest;
import com.seos.pmis.task.dto.request.TaskSearchRequest;
import com.seos.pmis.task.dto.request.TaskUpdateRequest;
import com.seos.pmis.task.dto.response.TaskResponse;
import com.seos.pmis.task.entity.Task;
import com.seos.pmis.task.entity.TaskPriority;
import com.seos.pmis.task.entity.TaskStatus;
import com.seos.pmis.task.repository.TaskRepository;
import com.seos.pmis.task.specification.TaskSpecification;
import com.seos.pmis.user.entity.User;
import com.seos.pmis.user.repository.UserRepository;
import com.seos.pmis.wbs.entity.Wbs;
import com.seos.pmis.wbs.repository.WbsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskService {

    /**
     * Task 검색에서 허용하는 정렬 필드
     */
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id",
            "taskName",
            "status",
            "progressRate",
            "priority",
            "startDate",
            "endDate",
            "sortOrder",
            "createdAt",
            "updatedAt"
    );

    /*
     * Repository
     */
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final WbsRepository wbsRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;


    /**
     * Task 생성
     *
     * @param projectId 소속 프로젝트 ID
     * @param request Task 생성 요청
     * @return 생성된 Task 응답
     */
    @Transactional
    public TaskResponse create(Long projectId, TaskCreateRequest request) {

        if (projectId == null || projectId <= 0) {
            throw new IllegalArgumentException(
                    "Project ID는 양수여야 합니다."
            );
        }

        if (request == null) {
            throw new IllegalArgumentException(
                    "Task 생성 요청은 필수입니다."
            );
        }

        // 현재 로그인 사용자 및 담당자 지정 권한 확인
        UserPrincipal currentUser = getCurrentUserPrincipal();

        // Project 조회
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "존재하지 않는 프로젝트입니다. projectId=" + projectId
                ));

        // WBS 조회: 선택 사항
        Wbs wbs = request.getWbsId() == null
                ? null
                : wbsRepository.findById(request.getWbsId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "존재하지 않는 WBS입니다. wbsId="
                                        + request.getWbsId()
                        ));

        // WBS가 지정된 경우 Project 소속 확인
        if (wbs != null
                && !wbs.getProject().getId().equals(projectId)) {
            throw new IllegalArgumentException(
                    "WBS가 요청한 프로젝트에 속하지 않습니다."
            );
        }

        // Schedule 조회 및 Project/WBS 소속 확인
        Schedule schedule = request.getScheduleId() == null
                ? null
                : scheduleRepository.findById(request.getScheduleId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "존재하지 않는 Schedule입니다. scheduleId="
                                        + request.getScheduleId()
                        ));

        validateScheduleRelationship(
                schedule,
                projectId,
                wbs
        );

        // 담당자 조회
        User assignee = request.getAssigneeId() == null
                ? null
                : userRepository.findById(request.getAssigneeId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "존재하지 않는 사용자입니다. assigneeId="
                                        + request.getAssigneeId()
                        ));

        // 담당자 활성화 여부 및 지정 권한 확인
        validateAssignee(assignee, currentUser);

        // 상태 및 진척률 기본값 적용
        TaskStatus status = request.getStatus() != null
                ? request.getStatus()
                : TaskStatus.PLANNED;

        Integer progressRate = request.getProgressRate() != null
                ? request.getProgressRate()
                : 0;

        // 완료 상태와 진척률 일관성 유지
        if (status == TaskStatus.COMPLETED || progressRate == 100) {
            status = TaskStatus.COMPLETED;
            progressRate = 100;
        }

        // 우선순위 기본값 적용
        TaskPriority priority = request.getPriority() != null
                ? request.getPriority()
                : TaskPriority.MEDIUM;

        // Task 생성
        Task task = Task.builder()
                .project(project)
                .wbs(wbs)
                .schedule(schedule)
                .assignee(assignee)
                .taskName(request.getTaskName())
                .description(request.getDescription())
                .status(status)
                .progressRate(progressRate)
                .priority(priority)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .sortOrder(request.getSortOrder())
                .plannedManDays(request.getPlannedManDays())
                .build();

        return TaskResponse.from(taskRepository.save(task));
    }


    /**
     * Task 수정
     *
     * @param id Task ID
     * @param request Task 수정 요청
     * @return 수정된 Task 응답
     */
    @Transactional
    public TaskResponse update(Long id, TaskUpdateRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Task 수정 요청은 필수입니다."
            );
        }

        // 기존 Task 조회
        Task task = findTask(id);

        // 현재 로그인 사용자 및 Task 수정 권한 확인
        UserPrincipal currentUser = getCurrentUserPrincipal();
        validateTaskManagementPermission(task, currentUser);

        // 필수 입력값 검증
        if (request.getTaskName() == null
                || request.getTaskName().isBlank()
                || request.getTaskName().length() > 200) {
            throw new IllegalArgumentException(
                    "작업명은 필수이며 200자를 초과할 수 없습니다."
            );
        }

        if (request.getPriority() == null) {
            throw new IllegalArgumentException(
                    "작업 우선순위는 필수입니다."
            );
        }

        // 기존 Task의 Project ID
        Long projectId = task.getProject().getId();

        // 변경할 WBS 조회: 선택 사항
        Wbs wbs = request.getWbsId() == null
                ? null
                : wbsRepository.findById(request.getWbsId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "존재하지 않는 WBS입니다. wbsId="
                                        + request.getWbsId()
                        ));

        // WBS가 지정된 경우 기존 Project 소속 검증
        if (wbs != null
                && !wbs.getProject().getId().equals(projectId)) {
            throw new IllegalArgumentException(
                    "WBS가 기존 Task의 프로젝트에 속하지 않습니다."
            );
        }

        // 변경할 Schedule 조회 및 소속 관계 검증
        Schedule schedule = request.getScheduleId() == null
                ? null
                : scheduleRepository.findById(request.getScheduleId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "존재하지 않는 Schedule입니다. scheduleId="
                                        + request.getScheduleId()
                        ));

        validateScheduleRelationship(
                schedule,
                projectId,
                wbs
        );

        // 변경할 담당자 조회
        User assignee = request.getAssigneeId() == null
                ? null
                : userRepository.findById(request.getAssigneeId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "존재하지 않는 사용자입니다. assigneeId="
                                        + request.getAssigneeId()
                        ));

        // 담당자 활성화 여부 및 지정 권한 확인
        validateAssignee(assignee, currentUser);

        // Task 기본 정보 수정
        task.update(
                request.getTaskName(),
                request.getDescription(),
                request.getPriority(),
                request.getStartDate(),
                request.getEndDate()
        );

        // WBS, Schedule, 담당자 변경
        // null인 경우 기존 연결을 해제한다.
        task.changeWbs(wbs);
        task.changeSchedule(schedule);
        task.changeAssignee(assignee);

        // 진척률은 요청값이 있는 경우에만 변경
        if (request.getProgressRate() != null) {
            task.changeProgressRate(request.getProgressRate());
        }

        // 표시 순서는 요청값이 있는 경우에만 변경
        if (request.getSortOrder() != null) {
            task.changeSortOrder(request.getSortOrder());
        }

        // 계획 공수: null이면 기존 값 초기화
        task.changePlannedManDays(request.getPlannedManDays());

        return TaskResponse.from(task);
    }


    /**
     * Task 상태 변경
     *
     * @param id Task ID
     * @param status 변경할 작업 상태
     * @return 상태가 변경된 Task 응답
     */
    @Transactional
    public TaskResponse changeStatus(Long id, TaskStatus status) {

        if (status == null) {
            throw new IllegalArgumentException(
                    "변경할 작업 상태는 필수입니다."
            );
        }

        // 상태 변경 대상 Task 조회
        Task task = findTask(id);

        // 현재 로그인 사용자 및 Task 수정 권한 확인
        UserPrincipal currentUser = getCurrentUserPrincipal();
        validateTaskManagementPermission(task, currentUser);

        // Task 상태 변경
        task.changeStatus(status);

        return TaskResponse.from(task);
    }


    /**
     * Task 삭제
     *
     * @param id Task ID
     */
    @Transactional
    public void delete(Long id) {

        // 삭제 대상 Task 조회
        Task task = findTask(id);

        // 현재 로그인 사용자 및 Task 삭제 권한 확인
        UserPrincipal currentUser = getCurrentUserPrincipal();
        validateTaskManagementPermission(task, currentUser);

        // Task 삭제
        taskRepository.delete(task);
    }


    /**
     * Task 단건 조회
     *
     * @param id Task ID
     * @return Task 응답
     */
    public TaskResponse findById(Long id) {

        Task task = findTask(id);

        return TaskResponse.from(task);
    }


    /**
     * 프로젝트별 Task 목록 조회
     *
     * @param projectId 프로젝트 ID
     * @return Task 응답 목록
     */
    public List<TaskResponse> findByProjectId(Long projectId) {

        if (projectId == null || projectId <= 0) {
            throw new IllegalArgumentException(
                    "Project ID는 양수여야 합니다."
            );
        }

        if (!projectRepository.existsById(projectId)) {
            throw new IllegalArgumentException(
                    "존재하지 않는 프로젝트입니다. projectId=" + projectId
            );
        }

        return taskRepository.findByProject_IdOrderBySortOrderAsc(projectId)
                .stream()
                .map(TaskResponse::from)
                .toList();
    }


    /**
     * WBS별 Task 목록 조회
     *
     * @param wbsId WBS ID
     * @return Task 응답 목록
     */
    public List<TaskResponse> findByWbsId(Long wbsId) {

        if (wbsId == null || wbsId <= 0) {
            throw new IllegalArgumentException(
                    "WBS ID는 양수여야 합니다."
            );
        }

        if (!wbsRepository.existsById(wbsId)) {
            throw new IllegalArgumentException(
                    "존재하지 않는 WBS입니다. wbsId=" + wbsId
            );
        }

        return taskRepository.findByWbs_IdOrderBySortOrderAsc(wbsId)
                .stream()
                .map(TaskResponse::from)
                .toList();
    }


    /**
     * Schedule별 Task 목록 조회
     *
     * @param scheduleId Schedule ID
     * @return Task 응답 목록
     */
    public List<TaskResponse> findByScheduleId(Long scheduleId) {

        if (scheduleId == null || scheduleId <= 0) {
            throw new IllegalArgumentException(
                    "Schedule ID는 양수여야 합니다."
            );
        }

        if (!scheduleRepository.existsById(scheduleId)) {
            throw new IllegalArgumentException(
                    "존재하지 않는 Schedule입니다. scheduleId=" + scheduleId
            );
        }

        return taskRepository.findBySchedule_IdOrderBySortOrderAsc(scheduleId)
                .stream()
                .map(TaskResponse::from)
                .toList();
    }


    /**
     * 담당자별 Task 목록 조회
     *
     * @param assigneeId 담당자 User ID
     * @return 담당 Task 목록
     */
    public List<TaskResponse> findByAssigneeId(Long assigneeId) {

        if (assigneeId == null || assigneeId <= 0) {
            throw new IllegalArgumentException(
                    "담당자 ID는 양수여야 합니다."
            );
        }

        if (!userRepository.existsById(assigneeId)) {
            throw new IllegalArgumentException(
                    "존재하지 않는 사용자입니다. assigneeId=" + assigneeId
            );
        }

        return taskRepository.findByAssignee_IdOrderByStartDateAsc(assigneeId)
                .stream()
                .map(TaskResponse::from)
                .toList();
    }


    /**
     * Task 검색
     *
     * @param request 검색 요청
     * @return Task 페이지
     */
    public Page<TaskResponse> search(TaskSearchRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Task 검색 요청은 필수입니다."
            );
        }

        validateSearchDateRange(
                request.getStartDate(),
                request.getEndDate()
        );

        Pageable pageable = createPageable(request);

        return taskRepository.findAll(
                        TaskSpecification.projectId(request.getProjectId())
                                .and(TaskSpecification.wbsId(request.getWbsId()))
                                .and(TaskSpecification.scheduleId(request.getScheduleId()))
                                .and(TaskSpecification.assigneeId(request.getAssigneeId()))
                                .and(TaskSpecification.keyword(request.getKeyword()))
                                .and(TaskSpecification.status(request.getStatus()))
                                .and(TaskSpecification.priority(request.getPriority()))
                                .and(TaskSpecification.startDateFrom(request.getStartDate()))
                                .and(TaskSpecification.endDateTo(request.getEndDate())),
                        pageable
                )
                .map(TaskResponse::from);
    }


    /**
     * 기간별 Task 검색
     *
     * @param request 검색 요청
     * @param searchStart 조회 시작일
     * @param searchEnd 조회 종료일
     * @return Task 페이지
     */
    public Page<TaskResponse> searchByPeriod(
            TaskSearchRequest request,
            LocalDate searchStart,
            LocalDate searchEnd
    ) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Task 검색 요청은 필수입니다."
            );
        }

        validateRequiredDateRange(searchStart, searchEnd);

        Pageable pageable = createPageable(request);

        return taskRepository.findAll(
                        TaskSpecification.projectId(request.getProjectId())
                                .and(TaskSpecification.wbsId(request.getWbsId()))
                                .and(TaskSpecification.scheduleId(request.getScheduleId()))
                                .and(TaskSpecification.assigneeId(request.getAssigneeId()))
                                .and(TaskSpecification.keyword(request.getKeyword()))
                                .and(TaskSpecification.status(request.getStatus()))
                                .and(TaskSpecification.priority(request.getPriority()))
                                .and(TaskSpecification.overlaps(searchStart, searchEnd)),
                        pageable
                )
                .map(TaskResponse::from);
    }


    /**
     * Schedule과 Task의 Project/WBS 관계 검증
     *
     * Schedule은 Task와 같은 Project에 속해야 한다.
     * Task에 WBS가 지정된 경우에만 Schedule의 WBS 일치 여부를 확인한다.
     */
    private void validateScheduleRelationship(
            Schedule schedule,
            Long projectId,
            Wbs wbs
    ) {

        if (schedule == null) {
            return;
        }

        if (!schedule.getProject().getId().equals(projectId)) {
            throw new IllegalArgumentException(
                    "Schedule이 요청한 프로젝트에 속하지 않습니다."
            );
        }

        if (wbs != null
                && (schedule.getWbs() == null
                || !schedule.getWbs().getId().equals(wbs.getId()))) {
            throw new IllegalArgumentException(
                    "Schedule과 Task의 WBS가 일치하지 않습니다."
            );
        }
    }


    /**
     * 현재 인증된 사용자 조회
     */
    private UserPrincipal getCurrentUserPrincipal() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof UserPrincipal userPrincipal)) {
            throw new BusinessException(CommonErrorCode.UNAUTHORIZED);
        }

        return userPrincipal;
    }


    /**
     * 담당자 지정 권한 및 활성화 여부 검증
     *
     * USER는 본인만 담당자로 지정할 수 있다.
     * PM/ADMIN은 다른 활성 사용자도 지정할 수 있다.
     * 담당자 null은 담당자 미지정 또는 해제를 의미한다.
     */
    private void validateAssignee(
            User assignee,
            UserPrincipal currentUser
    ) {

        String role = currentUser.getRole();

        if (!isUserRole(role)
                && !isPrivilegedRole(role)) {
            throw new BusinessException(CommonErrorCode.ACCESS_DENIED);
        }

        if (assignee == null) {
            return;
        }

        if (Boolean.FALSE.equals(assignee.getEnabled())) {
            throw new IllegalArgumentException(
                    "비활성화된 사용자는 Task 담당자로 지정할 수 없습니다."
            );
        }

        if (isUserRole(role)
                && !assignee.getId().equals(currentUser.getUserId())) {
            throw new BusinessException(CommonErrorCode.ACCESS_DENIED);
        }
    }


    /**
     * Task 수정/상태 변경/삭제 권한 검증
     *
     * USER는 본인이 담당하는 Task만 관리할 수 있다.
     * PM/ADMIN은 모든 Task를 관리할 수 있다.
     */
    private void validateTaskManagementPermission(
            Task task,
            UserPrincipal currentUser
    ) {

        String role = currentUser.getRole();

        if (isPrivilegedRole(role)) {
            return;
        }

        if (isUserRole(role)) {

            User assignee = task.getAssignee();

            if (assignee != null
                    && assignee.getId().equals(currentUser.getUserId())) {
                return;
            }
        }

        throw new BusinessException(CommonErrorCode.ACCESS_DENIED);
    }


    /**
     * 일반 사용자 역할 확인
     */
    private boolean isUserRole(String role) {
        return "ROLE_USER".equals(role);
    }


    /**
     * PM 또는 ADMIN 역할 확인
     */
    private boolean isPrivilegedRole(String role) {
        return "ROLE_PM".equals(role)
                || "ROLE_ADMIN".equals(role);
    }


    /**
     * 검색 요청에 따른 Pageable 생성
     */
    private Pageable createPageable(TaskSearchRequest request) {

        return SearchPageableFactory.create(
                request.getPage(),
                request.getSize(),
                request.getSortBy(),
                request.getDirection(),
                "sortOrder",
                Sort.Direction.ASC,
                ALLOWED_SORT_FIELDS
        );
    }


    /**
     * 검색 날짜 범위 검증
     */
    private void validateSearchDateRange(
            LocalDate startDate,
            LocalDate endDate
    ) {

        if (startDate != null
                && endDate != null
                && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException(
                    "검색 종료일은 검색 시작일보다 빠를 수 없습니다."
            );
        }
    }


    /**
     * 필수 조회 날짜 범위 검증
     */
    private void validateRequiredDateRange(
            LocalDate startDate,
            LocalDate endDate
    ) {

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException(
                    "조회 시작일과 종료일은 필수입니다."
            );
        }

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException(
                    "조회 종료일은 조회 시작일보다 빠를 수 없습니다."
            );
        }
    }


    /**
     * Task Entity 조회
     */
    private Task findTask(Long id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "Task ID는 양수여야 합니다."
            );
        }

        return taskRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "존재하지 않는 Task입니다. taskId=" + id
                        )
                );
    }
}
