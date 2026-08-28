package com.seos.pmis.schedule.service;

import com.seos.pmis.project.entity.Project;
import com.seos.pmis.project.repository.ProjectRepository;
import com.seos.pmis.schedule.dto.request.ScheduleCreateRequest;
import com.seos.pmis.schedule.dto.request.ScheduleSearchRequest;
import com.seos.pmis.schedule.dto.request.ScheduleUpdateRequest;
import com.seos.pmis.schedule.dto.response.ScheduleResponse;
import com.seos.pmis.schedule.entity.Schedule;
import com.seos.pmis.schedule.entity.ScheduleStatus;
import com.seos.pmis.schedule.repository.ScheduleRepository;
import com.seos.pmis.schedule.specification.ScheduleSpecification;
import com.seos.pmis.wbs.entity.Wbs;
import com.seos.pmis.wbs.repository.WbsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Schedule Service
 *
 * Schedule Domain의 비즈니스 로직을 담당한다.
 *
 * 주요 책임:
 * - Schedule 단건 조회
 * - Project별 Schedule 조회
 * - WBS별 Schedule 조회
 * - Schedule 검색
 * - Schedule 생성
 * - Schedule 수정
 * - Schedule 상태 변경
 * - Schedule 정렬 순서 변경
 * - Schedule 삭제
 * - Schedule Request Validation
 * - WBS와 Project 간 소속 관계 검증
 *
 * Entity 수정은 JPA Dirty Checking을 통해 반영한다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    private final ProjectRepository projectRepository;

    private final WbsRepository wbsRepository;

    /**
     * Schedule 단건 조회
     *
     * @param id Schedule ID
     * @return Schedule Response
     */
    public ScheduleResponse findById(Long id) {

        Schedule schedule = findSchedule(id);

        return ScheduleResponse.from(schedule);
    }

    /**
     * Project별 Schedule 조회
     *
     * 해당 Project에 속한 Schedule을
     * sortOrder 오름차순으로 조회한다.
     *
     * @param projectId Project ID
     * @return Schedule 목록
     */
    public List<ScheduleResponse> findByProjectId(
            Long projectId
    ) {

        validateProjectId(projectId);

        if (!projectRepository.existsById(projectId)) {
            throw new IllegalArgumentException(
                    "Project not found: " + projectId
            );
        }

        return scheduleRepository
                .findByProject_IdOrderBySortOrderAsc(projectId)
                .stream()
                .map(ScheduleResponse::from)
                .toList();
    }

    /**
     * WBS별 Schedule 조회
     *
     * 해당 WBS에 연결된 Schedule을
     * sortOrder 오름차순으로 조회한다.
     *
     * @param wbsId WBS ID
     * @return Schedule 목록
     */
    public List<ScheduleResponse> findByWbsId(
            Long wbsId
    ) {

        validateWbsId(wbsId);

        if (!wbsRepository.existsById(wbsId)) {
            throw new IllegalArgumentException(
                    "WBS not found: " + wbsId
            );
        }

        return scheduleRepository
                .findByWbs_IdOrderBySortOrderAsc(wbsId)
                .stream()
                .map(ScheduleResponse::from)
                .toList();
    }

    /**
     * Schedule 검색
     *
     * ScheduleSearchRequest의 조건을 조합하여
     * 동적 검색을 수행한다.
     *
     * 지원 조건:
     * - projectId
     * - wbsId
     * - keyword
     * - status
     * - page
     * - size
     * - sortBy
     * - direction
     *
     * @param request 검색 요청
     * @return Schedule Page
     */
    public Page<ScheduleResponse> search(
            ScheduleSearchRequest request
    ) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Schedule search request is required."
            );
        }

        validateSearchRequest(request);

        Pageable pageable = createPageable(request);

        return scheduleRepository.findAll(
                        ScheduleSpecification.projectId(
                                request.getProjectId()
                        ).and(
                                ScheduleSpecification.wbsId(
                                        request.getWbsId()
                                )
                        ).and(
                                ScheduleSpecification.keyword(
                                        request.getKeyword()
                                )
                        ).and(
                                ScheduleSpecification.status(
                                        request.getStatus()
                                )
                        ),
                        pageable
                )
                .map(ScheduleResponse::from);
    }

    /**
     * 기간 조건을 포함한 Schedule 검색
     *
     * 특정 기간과 겹치는 Schedule을 조회한다.
     *
     * @param request 검색 요청
     * @param searchStart 검색 시작일
     * @param searchEnd 검색 종료일
     * @return Schedule Page
     */
    public Page<ScheduleResponse> searchByPeriod(
            ScheduleSearchRequest request,
            LocalDate searchStart,
            LocalDate searchEnd
    ) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Schedule search request is required."
            );
        }

        validateSearchRequest(request);

        validateSearchPeriod(
                searchStart,
                searchEnd
        );

        Pageable pageable = createPageable(request);

        return scheduleRepository.findAll(
                        ScheduleSpecification.projectId(
                                request.getProjectId()
                        ).and(
                                ScheduleSpecification.wbsId(
                                        request.getWbsId()
                                )
                        ).and(
                                ScheduleSpecification.keyword(
                                        request.getKeyword()
                                )
                        ).and(
                                ScheduleSpecification.status(
                                        request.getStatus()
                                )
                        ).and(
                                ScheduleSpecification.overlaps(
                                        searchStart,
                                        searchEnd
                                )
                        ),
                        pageable
                )
                .map(ScheduleResponse::from);
    }

    /**
     * Schedule 생성
     *
     * Schedule은 반드시 특정 Project에 소속되며
     * 지정된 WBS 역시 해당 Project에 소속되어야 한다.
     *
     * @param projectId Project ID
     * @param request Schedule 생성 요청
     * @return 생성된 Schedule
     */
    @Transactional
    public ScheduleResponse create(
            Long projectId,
            ScheduleCreateRequest request
    ) {

        validateProjectId(projectId);

        validateRequest(request);

        Project project = findProject(projectId);

        Wbs wbs = validateWbsOwnership(
                projectId,
                request.getWbsId()
        );

        validateDateRange(
                request.getStartDate(),
                request.getEndDate()
        );

        Schedule schedule = Schedule.builder()
                .project(project)
                .wbs(wbs)
                .scheduleName(request.getScheduleName())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(request.getStatus())
                .sortOrder(request.getSortOrder())
                .description(request.getDescription())
                .build();

        Schedule savedSchedule =
                scheduleRepository.save(schedule);

        return ScheduleResponse.from(savedSchedule);
    }

    /**
     * Schedule 수정
     *
     * Schedule의 Project는 변경하지 않는다.
     *
     * WBS 변경이 필요한 경우
     * 기존 Project와 새로운 WBS의 소속 관계를 검증한 후
     * Entity의 changeWbs()를 사용한다.
     *
     * @param id Schedule ID
     * @param request Schedule 수정 요청
     * @return 수정된 Schedule
     */
    @Transactional
    public ScheduleResponse update(
            Long id,
            ScheduleUpdateRequest request
    ) {

        Schedule schedule = findSchedule(id);

        validateRequest(request);

        Wbs wbs = validateWbsOwnership(
                schedule.getProject().getId(),
                request.getWbsId()
        );

        validateDateRange(
                request.getStartDate(),
                request.getEndDate()
        );

        schedule.update(
                request.getScheduleName(),
                request.getStartDate(),
                request.getEndDate(),
                request.getStatus(),
                request.getSortOrder(),
                request.getDescription()
        );

        schedule.changeWbs(wbs);

        return ScheduleResponse.from(schedule);
    }

    /**
     * Schedule 상태 변경
     *
     * @param id Schedule ID
     * @param status 변경할 상태
     * @return 변경된 Schedule
     */
    @Transactional
    public ScheduleResponse changeStatus(
            Long id,
            ScheduleStatus status
    ) {

        Schedule schedule = findSchedule(id);

        validateStatus(status);

        schedule.changeStatus(status);

        return ScheduleResponse.from(schedule);
    }

    /**
     * Schedule 정렬 순서 변경
     *
     * @param id Schedule ID
     * @param sortOrder 새로운 정렬 순서
     * @return 변경된 Schedule
     */
    @Transactional
    public ScheduleResponse changeSortOrder(
            Long id,
            Integer sortOrder
    ) {

        Schedule schedule = findSchedule(id);

        validateSortOrder(sortOrder);

        schedule.changeSortOrder(sortOrder);

        return ScheduleResponse.from(schedule);
    }

    /**
     * Schedule 삭제
     *
     * 현재 Schedule 자체에는 하위 Domain이 없으므로
     * 기본적으로 삭제를 허용한다.
     *
     * 향후 Task / Progress 등이 Schedule에 연결될 경우
     * 삭제 정책을 확장한다.
     *
     * @param id Schedule ID
     */
    @Transactional
    public void delete(Long id) {

        Schedule schedule = findSchedule(id);

        scheduleRepository.delete(schedule);
    }

    /**
     * Schedule Entity 조회
     *
     * @param id Schedule ID
     * @return Schedule Entity
     */
    private Schedule findSchedule(Long id) {

        if (id == null) {
            throw new IllegalArgumentException(
                    "Schedule ID is required."
            );
        }

        return scheduleRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Schedule not found: " + id
                        )
                );
    }

    /**
     * Project Entity 조회
     *
     * @param projectId Project ID
     * @return Project Entity
     */
    private Project findProject(Long projectId) {

        return projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Project not found: " + projectId
                        )
                );
    }

    /**
     * Project ID Validation
     */
    private void validateProjectId(Long projectId) {

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
     * WBS ID Validation
     */
    private void validateWbsId(Long wbsId) {

        if (wbsId == null) {
            throw new IllegalArgumentException(
                    "WBS ID is required."
            );
        }

        if (wbsId <= 0) {
            throw new IllegalArgumentException(
                    "WBS ID must be greater than zero."
            );
        }
    }

    /**
     * Schedule Create Request Validation
     */
    private void validateRequest(
            ScheduleCreateRequest request
    ) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Schedule request is required."
            );
        }

        validateWbsId(request.getWbsId());

        validateScheduleName(
                request.getScheduleName()
        );

        validateSortOrder(
                request.getSortOrder()
        );

        validateStatus(
                request.getStatus()
        );
    }

    /**
     * Schedule Update Request Validation
     */
    private void validateRequest(
            ScheduleUpdateRequest request
    ) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Schedule request is required."
            );
        }

        validateWbsId(request.getWbsId());

        validateScheduleName(
                request.getScheduleName()
        );

        validateSortOrder(
                request.getSortOrder()
        );

        validateStatus(
                request.getStatus()
        );
    }

    /**
     * Schedule 검색 요청 Validation
     */
    private void validateSearchRequest(
            ScheduleSearchRequest request
    ) {

        if (request.getProjectId() != null) {
            validateProjectId(
                    request.getProjectId()
            );
        }

        if (request.getWbsId() != null) {
            validateWbsId(
                    request.getWbsId()
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
     * 기본 정렬은 sortOrder ASC이다.
     *
     * 허용 정렬 필드:
     * - id
     * - scheduleName
     * - startDate
     * - endDate
     * - sortOrder
     * - createdAt
     * - updatedAt
     */
    private Pageable createPageable(
            ScheduleSearchRequest request
    ) {

        String sortBy = normalizeSortBy(
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
                 "scheduleName",
                 "startDate",
                 "endDate",
                 "sortOrder",
                 "createdAt",
                 "updatedAt" ->
                    sortBy;

            default ->
                    throw new IllegalArgumentException(
                            "Unsupported sort field: " + sortBy
                    );
        };
    }

    /**
     * 정렬 방향 Validation
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
     * Schedule 이름 Validation
     */
    private void validateScheduleName(
            String scheduleName
    ) {

        if (scheduleName == null ||
                scheduleName.isBlank()) {

            throw new IllegalArgumentException(
                    "Schedule name is required."
            );
        }

        if (scheduleName.length() > 200) {

            throw new IllegalArgumentException(
                    "Schedule name must not exceed 200 characters."
            );
        }
    }

    /**
     * Schedule Status Validation
     */
    private void validateStatus(
            ScheduleStatus status
    ) {

        if (status == null) {

            throw new IllegalArgumentException(
                    "Schedule status is required."
            );
        }
    }

    /**
     * Sort Order Validation
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
     * 시작일 / 종료일 Validation
     *
     * 종료일은 시작일보다 빠를 수 없다.
     */
    private void validateDateRange(
            LocalDate startDate,
            LocalDate endDate
    ) {

        if (startDate == null) {

            throw new IllegalArgumentException(
                    "Start date is required."
            );
        }

        if (endDate == null) {

            throw new IllegalArgumentException(
                    "End date is required."
            );
        }

        if (endDate.isBefore(startDate)) {

            throw new IllegalArgumentException(
                    "End date must not be before start date."
            );
        }
    }

    /**
     * 검색 기간 Validation
     */
    private void validateSearchPeriod(
            LocalDate searchStart,
            LocalDate searchEnd
    ) {

        if (searchStart == null) {

            throw new IllegalArgumentException(
                    "Search start date is required."
            );
        }

        if (searchEnd == null) {

            throw new IllegalArgumentException(
                    "Search end date is required."
            );
        }

        if (searchEnd.isBefore(searchStart)) {

            throw new IllegalArgumentException(
                    "Search end date must not be before search start date."
            );
        }
    }

    /**
     * WBS와 Project의 소속 관계 Validation
     *
     * Schedule이 Project 1에 속하면서
     * Project 2의 WBS를 참조하는 것을 방지한다.
     *
     * @param projectId Project ID
     * @param wbsId WBS ID
     * @return 검증된 WBS Entity
     */
    private Wbs validateWbsOwnership(
            Long projectId,
            Long wbsId
    ) {

        validateProjectId(projectId);
        validateWbsId(wbsId);

        Wbs wbs = wbsRepository.findById(wbsId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "WBS not found: " + wbsId
                        )
                );

        if (!projectId.equals(
                wbs.getProject().getId()
        )) {

            throw new IllegalArgumentException(
                    "WBS does not belong to the specified project."
            );
        }

        return wbs;
    }
}