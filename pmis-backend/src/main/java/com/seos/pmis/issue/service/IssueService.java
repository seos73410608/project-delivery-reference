package com.seos.pmis.issue.service;

import com.seos.pmis.issue.dto.request.IssueCreateRequest;
import com.seos.pmis.issue.dto.request.IssueSearchRequest;
import com.seos.pmis.issue.dto.request.IssueStatusUpdateRequest;
import com.seos.pmis.issue.dto.request.IssueUpdateRequest;
import com.seos.pmis.issue.dto.response.IssueResponse;
import com.seos.pmis.issue.entity.Issue;
import com.seos.pmis.issue.entity.IssuePriority;
import com.seos.pmis.issue.entity.IssueStatus;
import com.seos.pmis.issue.repository.IssueRepository;
import com.seos.pmis.issue.specification.IssueSpecification;
import com.seos.pmis.project.entity.Project;
import com.seos.pmis.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Issue Service
 *
 * Issue Domain의 비즈니스 로직을 담당한다.
 *
 * 주요 책임:
 *
 * - Issue 단건 조회
 * - Project별 Issue 조회
 * - Issue 검색
 * - Issue 생성
 * - Issue 수정
 * - Issue 상태 변경
 * - Issue 정렬 순서 변경
 * - Issue 삭제
 * - Request Validation
 * - 날짜 Validation
 * - Project 존재 여부 검증
 * - Issue 상태 전이 검증
 *
 * Entity 수정은 JPA Dirty Checking을 통해 반영한다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IssueService {

    private final IssueRepository issueRepository;

    private final ProjectRepository projectRepository;


    /**
     * Issue 단건 조회
     *
     * @param id Issue ID
     * @return Issue Response
     */
    public IssueResponse findById(
            Long id
    ) {

        Issue issue = findIssue(id);

        return IssueResponse.from(issue);
    }


    /**
     * Project별 Issue 조회
     *
     * 해당 Project에 속한 Issue를
     * sortOrder 오름차순으로 조회한다.
     *
     * @param projectId Project ID
     * @return Issue 목록
     */
    public List<IssueResponse> findByProjectId(
            Long projectId
    ) {

        validateProjectId(projectId);

        findProject(projectId);

        return issueRepository
                .findByProject_IdOrderBySortOrderAsc(projectId)
                .stream()
                .map(IssueResponse::from)
                .toList();
    }


    /**
     * Issue 검색
     *
     * IssueSearchRequest의 조건을 조합하여
     * 동적 검색을 수행한다.
     *
     * 지원 조건:
     *
     * - projectId
     * - keyword
     * - status
     * - priority
     * - assigneeId
     * - page
     * - size
     * - sortBy
     * - direction
     *
     * @param request 검색 요청
     * @return Issue Page
     */
    public Page<IssueResponse> search(
            IssueSearchRequest request
    ) {

        if (request == null) {

            throw new IllegalArgumentException(
                    "Issue search request is required."
            );
        }

        validateSearchRequest(request);

        Pageable pageable =
                createPageable(request);

        Specification<Issue> specification =
                Specification.allOf(
                        List.of(
                                IssueSpecification.projectId(
                                        request.getProjectId()
                                ),
                                IssueSpecification.keyword(
                                        request.getKeyword()
                                ),
                                IssueSpecification.status(
                                        request.getStatus()
                                ),
                                IssueSpecification.priority(
                                        request.getPriority()
                                ),
                                IssueSpecification.assigneeId(
                                        request.getAssigneeId()
                                )
                        )
                );

        return issueRepository
                .findAll(
                        specification,
                        pageable
                )
                .map(IssueResponse::from);
    }


    /**
     * Issue 생성
     *
     * Issue는 반드시 특정 Project에
     * 소속되어야 한다.
     *
     * @param projectId Project ID
     * @param request Issue 생성 요청
     * @return 생성된 Issue
     */
    @Transactional
    public IssueResponse create(
            Long projectId,
            IssueCreateRequest request
    ) {

        validateProjectId(projectId);

        validateRequest(request);

        Project project =
                findProject(projectId);

        Issue issue = Issue.builder()
                .project(project)
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .priority(request.getPriority())
                .assigneeId(request.getAssigneeId())
                .occurredDate(request.getOccurredDate())
                .dueDate(request.getDueDate())
                .sortOrder(request.getSortOrder())
                .build();

        applyResolvedDate(
                issue,
                request.getStatus()
        );

        Issue savedIssue =
                issueRepository.save(issue);

        return IssueResponse.from(savedIssue);
    }


    /**
     * Issue 수정
     *
     * Project는 변경하지 않는다.
     *
     * Issue Key와 Reporter는
     * 별도 정책에 따라 관리한다.
     *
     * @param id Issue ID
     * @param request Issue 수정 요청
     * @return 수정된 Issue
     */
    @Transactional
    public IssueResponse update(
            Long id,
            IssueUpdateRequest request
    ) {

        Issue issue = findIssue(id);

        validateRequest(request);

        validateStatusTransition(
                issue.getStatus(),
                request.getStatus()
        );

        issue.update(
                request.getTitle(),
                request.getDescription(),
                request.getStatus(),
                request.getPriority(),
                request.getAssigneeId(),
                request.getOccurredDate(),
                request.getDueDate(),
                request.getSortOrder()
        );

        applyResolvedDate(
                issue,
                request.getStatus()
        );

        return IssueResponse.from(issue);
    }


    /**
     * Issue 상태 변경
     *
     * 상태 변경 시 상태 전이 규칙을 검증한다.
     *
     * RESOLVED 상태가 되면
     * resolvedDate를 자동 설정한다.
     *
     * @param id Issue ID
     * @param request 상태 변경 요청
     * @return 변경된 Issue
     */
    @Transactional
    public IssueResponse changeStatus(
            Long id,
            IssueStatusUpdateRequest request
    ) {

        Issue issue = findIssue(id);

        if (request == null) {

            throw new IllegalArgumentException(
                    "Issue status request is required."
            );
        }

        validateStatus(
                request.getStatus()
        );

        validateStatusTransition(
                issue.getStatus(),
                request.getStatus()
        );

        issue.changeStatus(
                request.getStatus()
        );

        applyResolvedDate(
                issue,
                request.getStatus()
        );

        return IssueResponse.from(issue);
    }


    /**
     * Issue 표시 순서 변경
     *
     * @param id Issue ID
     * @param sortOrder 새로운 표시 순서
     * @return 변경된 Issue
     */
    @Transactional
    public IssueResponse changeSortOrder(
            Long id,
            Integer sortOrder
    ) {

        Issue issue = findIssue(id);

        validateSortOrder(sortOrder);

        issue.changeSortOrder(sortOrder);

        return IssueResponse.from(issue);
    }


    /**
     * Issue 삭제
     *
     * 현재 Issue의 하위 Domain은
     * V1에서 구현하지 않는다.
     *
     * 향후 Comment, Attachment,
     * History 등이 연결될 경우
     * 삭제 정책을 확장할 수 있다.
     *
     * @param id Issue ID
     */
    @Transactional
    public void delete(
            Long id
    ) {

        Issue issue = findIssue(id);

        issueRepository.delete(issue);
    }


    /**
     * Issue Entity 조회
     *
     * @param id Issue ID
     * @return Issue Entity
     */
    private Issue findIssue(
            Long id
    ) {

        validateIssueId(id);

        return issueRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Issue not found: " + id
                        )
                );
    }


    /**
     * Project Entity 조회
     *
     * @param projectId Project ID
     * @return Project Entity
     */
    private Project findProject(
            Long projectId
    ) {

        return projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Project not found: " + projectId
                        )
                );
    }


    /**
     * Issue ID Validation
     *
     * @param id Issue ID
     */
    private void validateIssueId(
            Long id
    ) {

        if (id == null) {

            throw new IllegalArgumentException(
                    "Issue ID is required."
            );
        }

        if (id <= 0) {

            throw new IllegalArgumentException(
                    "Issue ID must be greater than zero."
            );
        }
    }


    /**
     * Project ID Validation
     *
     * @param projectId Project ID
     */
    private void validateProjectId(
            Long projectId
    ) {

        if (projectId == null) {

            throw new IllegalArgumentException(
                    "Project ID is required."
            );
        }

        if (projectId <= 0) {

            throw new IllegalArgumentException(
                    "Project ID must be greater than zero."
            );
        }
    }


    /**
     * Issue Create Request Validation
     *
     * @param request Issue 생성 요청
     */
    private void validateRequest(
            IssueCreateRequest request
    ) {

        if (request == null) {

            throw new IllegalArgumentException(
                    "Issue request is required."
            );
        }

        validateTitle(
                request.getTitle()
        );

        validateStatus(
                request.getStatus()
        );

        validatePriority(
                request.getPriority()
        );

        validateAssigneeId(
                request.getAssigneeId()
        );

        validateDateRange(
                request.getOccurredDate(),
                request.getDueDate()
        );

        validateSortOrder(
                request.getSortOrder()
        );
    }


    /**
     * Issue Update Request Validation
     *
     * @param request Issue 수정 요청
     */
    private void validateRequest(
            IssueUpdateRequest request
    ) {

        if (request == null) {

            throw new IllegalArgumentException(
                    "Issue request is required."
            );
        }

        validateTitle(
                request.getTitle()
        );

        validateStatus(
                request.getStatus()
        );

        validatePriority(
                request.getPriority()
        );

        validateAssigneeId(
                request.getAssigneeId()
        );

        validateDateRange(
                request.getOccurredDate(),
                request.getDueDate()
        );

        validateSortOrder(
                request.getSortOrder()
        );
    }


    /**
     * Issue 검색 요청 Validation
     *
     * @param request 검색 요청
     */
    private void validateSearchRequest(
            IssueSearchRequest request
    ) {

        if (request.getProjectId() != null) {

            validateProjectId(
                    request.getProjectId()
            );
        }

        if (request.getAssigneeId() != null) {

            validateAssigneeId(
                    request.getAssigneeId()
            );
        }

        if (request.getPage() == null ||
                request.getPage() < 0) {

            throw new IllegalArgumentException(
                    "Page must be greater than or equal to zero."
            );
        }

        if (request.getSize() == null ||
                request.getSize() < 1) {

            throw new IllegalArgumentException(
                    "Size must be greater than zero."
            );
        }

        if (request.getSize() > 100) {

            throw new IllegalArgumentException(
                    "Size must not exceed 100."
            );
        }
    }


    /**
     * Pageable 생성
     *
     * 기본 정렬은
     * sortOrder ASC이다.
     *
     * 허용 정렬 필드:
     *
     * - id
     * - issueKey
     * - title
     * - status
     * - priority
     * - occurredDate
     * - dueDate
     * - resolvedDate
     * - sortOrder
     * - createdAt
     * - updatedAt
     *
     * @param request 검색 요청
     * @return Pageable
     */
    private Pageable createPageable(
            IssueSearchRequest request
    ) {

        String sortBy =
                normalizeSortBy(
                        request.getSortBy()
                );

        Sort.Direction direction =
                parseDirection(
                        request.getDirection()
                );

        Sort sort = Sort.by(
                direction,
                sortBy
        );

        return PageRequest.of(
                request.getPage(),
                request.getSize(),
                sort
        );
    }


    /**
     * 정렬 필드 Validation
     *
     * @param sortBy 정렬 필드
     * @return 허용된 정렬 필드
     */
    private String normalizeSortBy(
            String sortBy
    ) {

        if (sortBy == null ||
                sortBy.isBlank()) {

            return "sortOrder";
        }

        return switch (sortBy) {

            case "id",
                 "issueKey",
                 "title",
                 "status",
                 "priority",
                 "occurredDate",
                 "dueDate",
                 "resolvedDate",
                 "sortOrder",
                 "createdAt",
                 "updatedAt" -> sortBy;

            default ->
                    throw new IllegalArgumentException(
                            "Unsupported sort field: "
                                    + sortBy
                    );
        };
    }


    /**
     * 정렬 방향 Validation
     *
     * @param direction 정렬 방향
     * @return Sort Direction
     */
    private Sort.Direction parseDirection(
            String direction
    ) {

        if (direction == null ||
                direction.isBlank()) {

            return Sort.Direction.ASC;
        }

        try {

            return Sort.Direction.fromString(
                    direction
            );

        } catch (IllegalArgumentException e) {

            throw new IllegalArgumentException(
                    "Direction must be ASC or DESC."
            );
        }
    }


    /**
     * Issue 제목 Validation
     *
     * @param title Issue 제목
     */
    private void validateTitle(
            String title
    ) {

        if (title == null ||
                title.isBlank()) {

            throw new IllegalArgumentException(
                    "Issue title is required."
            );
        }

        if (title.length() > 200) {

            throw new IllegalArgumentException(
                    "Issue title must not exceed "
                            + "200 characters."
            );
        }
    }


    /**
     * Issue Status Validation
     *
     * @param status Issue 상태
     */
    private void validateStatus(
            IssueStatus status
    ) {

        if (status == null) {

            throw new IllegalArgumentException(
                    "Issue status is required."
            );
        }
    }


    /**
     * Issue Priority Validation
     *
     * @param priority Issue 중요도
     */
    private void validatePriority(
            IssuePriority priority
    ) {

        if (priority == null) {

            throw new IllegalArgumentException(
                    "Issue priority is required."
            );
        }
    }


    /**
     * Assignee ID Validation
     *
     * 담당자는 선택 값이다.
     *
     * 값이 존재하는 경우
     * 0보다 커야 한다.
     *
     * @param assigneeId 담당자 ID
     */
    private void validateAssigneeId(
            Long assigneeId
    ) {

        if (assigneeId == null) {

            return;
        }

        if (assigneeId <= 0) {

            throw new IllegalArgumentException(
                    "Assignee ID must be greater than zero."
            );
        }
    }


    /**
     * Sort Order Validation
     *
     * V1에서는 필수 값으로 관리한다.
     *
     * @param sortOrder 표시 순서
     */
    private void validateSortOrder(
            Integer sortOrder
    ) {

        if (sortOrder == null) {

            throw new IllegalArgumentException(
                    "Sort order is required."
            );
        }

        if (sortOrder < 1) {

            throw new IllegalArgumentException(
                    "Sort order must be greater than zero."
            );
        }
    }


    /**
     * 발생일 / 조치 목표일 Validation
     *
     * 날짜는 선택 값이다.
     *
     * 두 날짜가 모두 존재하는 경우:
     *
     * occurredDate <= dueDate
     *
     * 를 만족해야 한다.
     *
     * @param occurredDate Issue 발생일
     * @param dueDate 조치 목표일
     */
    private void validateDateRange(
            LocalDate occurredDate,
            LocalDate dueDate
    ) {

        if (occurredDate == null ||
                dueDate == null) {

            return;
        }

        if (dueDate.isBefore(occurredDate)) {

            throw new IllegalArgumentException(
                    "Due date must not be before occurred date."
            );
        }
    }


    /**
     * Issue 상태 전이 Validation
     *
     * 기본 Lifecycle:
     *
     * OPEN
     *   ↓
     * IN_PROGRESS
     *   ↓
     * RESOLVED
     *   ↓
     * CLOSED
     *
     * 추가 상태:
     *
     * ON_HOLD
     * CANCELLED
     *
     * V1에서는 기본적인 상태 전이 규칙을 적용한다.
     *
     * @param currentStatus 현재 상태
     * @param newStatus 변경 상태
     */
    private void validateStatusTransition(
            IssueStatus currentStatus,
            IssueStatus newStatus
    ) {

        if (currentStatus == null ||
                newStatus == null) {

            throw new IllegalArgumentException(
                    "Issue status is required."
            );
        }

        if (currentStatus == newStatus) {

            return;
        }

        boolean allowed = switch (currentStatus) {

            case OPEN ->
                    newStatus == IssueStatus.IN_PROGRESS ||
                    newStatus == IssueStatus.ON_HOLD ||
                    newStatus == IssueStatus.CANCELLED;

            case IN_PROGRESS ->
                    newStatus == IssueStatus.RESOLVED ||
                    newStatus == IssueStatus.ON_HOLD ||
                    newStatus == IssueStatus.CANCELLED;

            case ON_HOLD ->
                    newStatus == IssueStatus.OPEN ||
                    newStatus == IssueStatus.IN_PROGRESS ||
                    newStatus == IssueStatus.CANCELLED;

            case RESOLVED ->
                    newStatus == IssueStatus.CLOSED ||
                    newStatus == IssueStatus.IN_PROGRESS;

            case CLOSED ->
                    false;

            case CANCELLED ->
                    false;
        };

        if (!allowed) {

            throw new IllegalArgumentException(
                    "Invalid Issue status transition: "
                            + currentStatus
                            + " -> "
                            + newStatus
            );
        }
    }


    /**
     * Issue 해결일 처리
     *
     * RESOLVED 상태가 되는 경우
     * resolvedDate가 없으면 오늘 날짜를 설정한다.
     *
     * RESOLVED가 아닌 상태가 되는 경우:
     *
     * - OPEN
     * - IN_PROGRESS
     * - ON_HOLD
     * - CANCELLED
     *
     * resolvedDate를 null로 초기화한다.
     *
     * CLOSED 상태는 RESOLVED 이후
     * 종료된 상태이므로 기존 해결일을 유지한다.
     *
     * @param issue Issue Entity
     * @param status 변경 상태
     */
    private void applyResolvedDate(
            Issue issue,
            IssueStatus status
    ) {

        if (status == IssueStatus.RESOLVED) {

            if (issue.getResolvedDate() == null) {

                issue.changeResolvedDate(
                        LocalDate.now()
                );
            }

            return;
        }

        if (status == IssueStatus.CLOSED) {

            return;
        }

        issue.changeResolvedDate(null);
    }
}